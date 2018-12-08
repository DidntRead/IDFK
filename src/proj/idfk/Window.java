package proj.idfk;

import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import proj.idfk.callback.*;
import proj.idfk.util.Disposable;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Window implements Disposable {
    private long handle;
    private Vector2i framebufferSize;
    private Vector2i windowSize;

    private CharCallback charCallback = null;
    private KeyCallback keyCallback = null;
    private ScrollCallback scrollCallback = null;
    private MouseButtonCallback mouseButtonCallback = null;

    private DoubleBuffer x = MemoryUtil.memAllocDouble(1);
    private DoubleBuffer y = MemoryUtil.memAllocDouble(1);

    public Window(String name, Config config) {
        this(name, config, false);
    }

    public Window(String name, Config config, boolean debug) {
        System.out.println("GLFW Version: " + glfwGetVersionString());

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwInit();

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, debug ? GLFW_TRUE : GLFW_FALSE);

        handle = glfwCreateWindow(config.width, config.height, name, config.fullscreen ? glfwGetPrimaryMonitor() : 0, 0);

        glfwMakeContextCurrent(handle);

        if (config.vsync) {
            glfwSwapInterval(1);
        }

        GL.createCapabilities();

        GL11.glClearColor(0, 0, 0.1f, 1);

        try (MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer x = stack.callocInt(1);
            IntBuffer y = stack.callocInt(1);
            glfwGetWindowSize(handle, x, y);
            this.windowSize = new Vector2i(x.get(0), y.get(0));
            glfwGetFramebufferSize(handle, x, y);
            this.framebufferSize = new Vector2i(x.get(0), y.get(0));
        }

        glfwSetCharCallback(handle, new GLFWCharCallback() {
            @Override
            public void invoke(long window, int codepoint) {
                if (charCallback != null) {
                    charCallback.charCallback((char) codepoint);
                }
            }
        });

        glfwSetKeyCallback(handle, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (keyCallback != null) {
                    keyCallback.keyCallback(key, action, mods);
                }
            }
        });

        glfwSetScrollCallback(handle, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                if (scrollCallback != null) {
                    scrollCallback.scrollCallback((float) yoffset);
                }
            }
        });

        glfwSetMouseButtonCallback(handle, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (mouseButtonCallback != null) {
                    glfwGetCursorPos(handle, x, y);
                    mouseButtonCallback.mouseButtonCallback((int)x.get(0), (int)y.get(0), button, action == GLFW_PRESS);
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

    public Vector2i getWindowSize() {
        return this.windowSize;
    }

    public Vector2i getFramebufferSize() {
        return this.framebufferSize;
    }

    @Override
    public void dispose() {
        MemoryUtil.memFree(x);
        MemoryUtil.memFree(y);
        glfwDestroyWindow(handle);
        glfwTerminate();
    }
}
