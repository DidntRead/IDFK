package proj.idfk;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import proj.idfk.callback.CharCallback;
import proj.idfk.callback.KeyCallback;
import proj.idfk.callback.MouseButtonCallback;
import proj.idfk.callback.ScrollCallback;
import proj.idfk.util.Disposable;
import proj.idfk.util.Resolution;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;

public class Window implements Disposable {
    private final long handle;
    private final Vector2i framebufferSize;
    private final Vector2i windowSize;
    private final Vector2f cursorPosition;
    private final Vector2f NDCPosition;
    private Callback debugCallback = null;

    private CharCallback charCallback = null;
    private KeyCallback keyCallback = null;
    private ScrollCallback scrollCallback = null;
    private MouseButtonCallback mouseButtonCallback = null;

    private final DoubleBuffer x = MemoryUtil.memAllocDouble(1);
    private final DoubleBuffer y = MemoryUtil.memAllocDouble(1);

    private NkContext ctx = null;
    private NkContext backup = null;

    public Window(String name, Config config) {
        this(name, config, false);
    }

    public Window(String name, Config config, boolean debug) {
        System.out.println("GLFW Version: " + glfwGetVersionString());

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwInit();

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, debug ? GLFW_TRUE : GLFW_FALSE);

        handle = glfwCreateWindow(config.width, config.height, name, config.fullscreen ? glfwGetPrimaryMonitor() : 0, 0);

        glfwMakeContextCurrent(handle);

        if (config.vsync) {
            glfwSwapInterval(1);
        }

        GL.createCapabilities();

        this.debugCallback = GLUtil.setupDebugMessageCallback(System.err);

        GL11.glClearColor(0, 0, 0.1f, 1);

        try (MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer x = stack.callocInt(1);
            IntBuffer y = stack.callocInt(1);
            glfwGetWindowSize(handle, x, y);
            this.windowSize = new Vector2i(x.get(0), y.get(0));
            glfwGetFramebufferSize(handle, x, y);
            this.framebufferSize = new Vector2i(x.get(0), y.get(0));
            DoubleBuffer mouseX = stack.callocDouble(1);
            DoubleBuffer mouseY = stack.callocDouble(1);
            glfwGetCursorPos(handle, mouseX, mouseY);
            this.cursorPosition = new Vector2f((float)mouseX.get(0), (float)mouseY.get(0));
            this.NDCPosition = new Vector2f((2.0f * cursorPosition.x) / windowSize.x - 1.0f, 1.0f - (2.0f * cursorPosition.y) / windowSize.y);
        }

        glfwSetCharCallback(handle, new GLFWCharCallback() {
            @Override
            public void invoke(long window, int codepoint) {
                if (charCallback != null) {
                    charCallback.charCallback((char) codepoint);
                }
                if (ctx != null) {
                    nk_input_unicode(ctx, codepoint);
                }
            }
        });

        glfwSetKeyCallback(handle, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (keyCallback != null) {
                    keyCallback.keyCallback(key, action, mods);
                }
                if (ctx != null) {
                    boolean press = action == GLFW_PRESS;
                    switch (key) {
                        case GLFW_KEY_DELETE:
                            nk_input_key(ctx, NK_KEY_DEL, press);
                            break;
                        case GLFW_KEY_ENTER:
                            nk_input_key(ctx, NK_KEY_ENTER, press);
                            break;
                        case GLFW_KEY_TAB:
                            nk_input_key(ctx, NK_KEY_TAB, press);
                            break;
                        case GLFW_KEY_BACKSPACE:
                            nk_input_key(ctx, NK_KEY_BACKSPACE, press);
                            break;
                        case GLFW_KEY_UP:
                            nk_input_key(ctx, NK_KEY_UP, press);
                            break;
                        case GLFW_KEY_DOWN:
                            nk_input_key(ctx, NK_KEY_DOWN, press);
                            break;
                        case GLFW_KEY_HOME:
                            nk_input_key(ctx, NK_KEY_TEXT_START, press);
                            nk_input_key(ctx, NK_KEY_SCROLL_START, press);
                            break;
                        case GLFW_KEY_END:
                            nk_input_key(ctx, NK_KEY_TEXT_END, press);
                            nk_input_key(ctx, NK_KEY_SCROLL_END, press);
                            break;
                        case GLFW_KEY_PAGE_DOWN:
                            nk_input_key(ctx, NK_KEY_SCROLL_DOWN, press);
                            break;
                        case GLFW_KEY_PAGE_UP:
                            nk_input_key(ctx, NK_KEY_SCROLL_UP, press);
                            break;
                        case GLFW_KEY_LEFT_SHIFT:
                        case GLFW_KEY_RIGHT_SHIFT:
                            nk_input_key(ctx, NK_KEY_SHIFT, press);
                            break;
                        case GLFW_KEY_LEFT_CONTROL:
                        case GLFW_KEY_RIGHT_CONTROL:
                            if (press) {
                                nk_input_key(ctx, NK_KEY_COPY, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                                nk_input_key(ctx, NK_KEY_PASTE, glfwGetKey(window, GLFW_KEY_P) == GLFW_PRESS);
                                nk_input_key(ctx, NK_KEY_CUT, glfwGetKey(window, GLFW_KEY_X) == GLFW_PRESS);
                                nk_input_key(ctx, NK_KEY_TEXT_UNDO, glfwGetKey(window, GLFW_KEY_Z) == GLFW_PRESS);
                                nk_input_key(ctx, NK_KEY_TEXT_REDO, glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS);
                                nk_input_key(ctx, NK_KEY_TEXT_WORD_LEFT, glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS);
                                nk_input_key(ctx, NK_KEY_TEXT_WORD_RIGHT, glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS);
                                nk_input_key(ctx, NK_KEY_TEXT_LINE_START, glfwGetKey(window, GLFW_KEY_B) == GLFW_PRESS);
                                nk_input_key(ctx, NK_KEY_TEXT_LINE_END, glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS);
                            } else {
                                nk_input_key(ctx, NK_KEY_LEFT, glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS);
                                nk_input_key(ctx, NK_KEY_RIGHT, glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS);
                                nk_input_key(ctx, NK_KEY_COPY, false);
                                nk_input_key(ctx, NK_KEY_PASTE, false);
                                nk_input_key(ctx, NK_KEY_CUT, false);
                                nk_input_key(ctx, NK_KEY_SHIFT, false);
                            }
                            break;
                    }
                }
            }
        });

        glfwSetScrollCallback(handle, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                if (scrollCallback != null) {
                    scrollCallback.scrollCallback((float) yoffset);
                }
                if (ctx != null) {
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        NkVec2 scroll = NkVec2.mallocStack(stack).x((float) xoffset).y((float) yoffset);
                        nk_input_scroll(ctx, scroll);
                    }
                }
            }
        });

        glfwSetCursorPosCallback(handle, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                if (ctx != null) {
                    nk_input_motion(ctx, (int) xpos, (int) ypos);
                }
                cursorPosition.set((float) xpos, (float) ypos);
                NDCPosition.set((float)((2.0f * xpos) / windowSize.x - 1.0f), (float)(1.0f - (2.0f * ypos) / windowSize.y));
            }
        });

        glfwSetMouseButtonCallback(handle, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (mouseButtonCallback != null) {
                    glfwGetCursorPos(handle, x, y);
                    mouseButtonCallback.mouseButtonCallback((int)x.get(0), (int)y.get(0), button, action == GLFW_PRESS);
                }
                if (ctx != null) {
                    glfwGetCursorPos(handle, x, y);
                    int nk_button;
                    switch (button) {
                        case GLFW_MOUSE_BUTTON_RIGHT:
                            nk_button = NK_BUTTON_RIGHT;
                            break;
                        case GLFW_MOUSE_BUTTON_MIDDLE:
                            nk_button = NK_BUTTON_MIDDLE;
                            break;
                        default:
                            nk_button = NK_BUTTON_LEFT;
                    }
                    nk_input_button(ctx, nk_button, (int)x.get(0), (int)y.get(0), action == GLFW_PRESS);
                }
            }
        });

        glfwSetFramebufferSizeCallback(handle, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                framebufferSize.set(width, height);
            }
        });

        glfwSetWindowSizeCallback(handle, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                windowSize.set(width, height);
            }
        });
    }

    public void setupNuklearContext(NkContext ctx) {
        this.ctx = ctx;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void poll() {
        glfwPollEvents();
    }

    public void display() {
        glfwSwapBuffers(handle);
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(handle, title);
    }

    public List<Resolution> getUsableResolutions() {
        List<Resolution> ret = new ArrayList<>();

        glfwGetVideoModes(glfwGetPrimaryMonitor()).forEach(mode -> {
            Resolution res = new Resolution(mode.width(), mode.height());
            if (!ret.contains(res)) {
                ret.add(res);
            }
        });

        return ret;
    }

    public void applySettings(Config config) {
        if (config.vsync) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }
    }

    public void registerCallbacks(Object o) {
        removeCallbacks();

        if (CharCallback.class.isAssignableFrom(o.getClass())) {
            charCallback = (CharCallback) o;
        }
        if (KeyCallback.class.isAssignableFrom(o.getClass())) {
            keyCallback = (KeyCallback) o;
        }
        if (ScrollCallback.class.isAssignableFrom(o.getClass())) {
            scrollCallback = (ScrollCallback) o;
        }
        if (MouseButtonCallback.class.isAssignableFrom(o.getClass())) {
            mouseButtonCallback = (MouseButtonCallback) o;
        }
    }

    public void removeCallbacks() {
        charCallback = null;
        keyCallback = null;
        scrollCallback = null;
        mouseButtonCallback = null;
    }

    public void disableNuklearInput() {
        backup = ctx;
        ctx = null;
    }

    public void enableNuklearInput() {
        ctx = backup;
        System.out.println(ctx == null);
        backup = null;
    }

    public void setCursorMode(boolean enabled) {
        glfwSetInputMode(handle, GLFW_CURSOR, enabled ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
    }

    public void setShouldClose() {
        glfwSetWindowShouldClose(handle, true);
    }

    public boolean isKeyDown(int key) {
        return glfwGetKey(handle, key) == GLFW_PRESS;
    }

    public boolean isMouseButtonDown(int button) {
        return glfwGetMouseButton(handle, button) == GLFW_PRESS;
    }

    public Vector2i getWindowSize() {
        return this.windowSize;
    }

    public Vector2i getFramebufferSize() {
        return this.framebufferSize;
    }

    public Vector2f getCursorPosition() {
        return this.cursorPosition;
    }

    public Vector2f getNDCPosition() {
        return this.NDCPosition;
    }

    @Override
    public void dispose() {
        if (debugCallback != null) {
            debugCallback.free();
        }
        MemoryUtil.memFree(x);
        MemoryUtil.memFree(y);
        glfwDestroyWindow(handle);
        glfwTerminate();
    }
}
