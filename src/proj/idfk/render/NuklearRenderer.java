package proj.idfk.render;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.nuklear.*;
import org.lwjgl.opengl.ARBBindlessTexture;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;
import proj.idfk.Application;
import proj.idfk.Window;
import proj.idfk.shader.NuklearShader;
import proj.idfk.util.Disposable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Objects;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL45.*;
import static proj.idfk.util.BufferUtil.ioResourceToByteBuffer;

public class NuklearRenderer implements Disposable {
    private final NkAllocator allocator;
    private final NkDrawVertexLayoutElement.Buffer VERTEX_LAYOUT;
    private NkUserFont default_font;
    private int fontTextureID;
    private NkImage background;
    private NkContext ctx;
    private NkBuffer cmds;
    private int vao, vbo, ebo;
    private NkConvertConfig convertConfig;
    private NkDrawNullTexture null_texture;
    private NuklearShader shader;
    private final HashMap<Integer, Long> textureHandles;
    private NkBuffer vbuf, ebuf;
    private final Window window;

    @SuppressWarnings("FieldCanBeLocal")
    private ByteBuffer ttf;

    public static NkColor grey;

    public NuklearRenderer(Application app) {
        this.textureHandles = new HashMap<>();
        this.window = app.getWindow();
        VERTEX_LAYOUT = NkDrawVertexLayoutElement.create(4)
                .position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0)
                .position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8)
                .position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16)
                .position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0)
                .flip();
        allocator = NkAllocator.create().alloc((handle, old, size) -> nmemAllocChecked(size)).mfree((handle,ptr) -> nmemFree(ptr));
        setupContext();
        initFont();
        setupNullTexture();
        setupRenderer();
    }

    public void render() {
        nk_buffer_clear(ebuf);
        nk_buffer_clear(vbuf);
        nk_convert(ctx, cmds, vbuf, ebuf, convertConfig);
        glBindVertexArray(vao);
        shader.bind();
        long offset = NULL;
        for (NkDrawCommand cmd = nk__draw_begin(ctx, cmds); cmd != null; cmd = nk__draw_next(cmd, cmds, ctx)) {
            if (cmd.elem_count() == 0) {
                continue;
            }
            shader.loadTextureHandle(textureHandles.get(cmd.texture().id()));
            glScissor(
                    (int)(cmd.clip_rect().x()),
                    ((window.getWindowSize().y - (int)(cmd.clip_rect().y() + cmd.clip_rect().h()))),
                    (int)(cmd.clip_rect().w()),
                    (int)(cmd.clip_rect().h())
            );
            glDrawElements(GL_TRIANGLES, cmd.elem_count(), GL_UNSIGNED_SHORT, offset);
            offset += cmd.elem_count() * 2;
        }
        nk_clear(ctx);
    }

    public NkContext getContext() {
        return this.ctx;
    }

    public NkImage getBackground() {
        return this.background;
    }

    public void beginInput() {
        nk_input_begin(ctx);
    }

    public void endInput() {
        nk_input_end(ctx);
    }

    private void setupNullTexture() {
        null_texture = NkDrawNullTexture.create();
        int id = glCreateTextures(GL_TEXTURE_2D);
        null_texture.texture().id(id);
        null_texture.uv().set(0.5f, 0.5f);
        glTextureParameteri(id, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTextureParameteri(id, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTextureStorage2D(id, 1, GL_RGBA8, 1, 1);
        try (MemoryStack stack = stackPush()) {
            glTextureSubImage2D(id, 0, 0, 0, 1, 1, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF));
        }
        textureHandles.put(id, ARBBindlessTexture.glGetTextureHandleARB(id));
        ARBBindlessTexture.glMakeTextureHandleResidentARB(textureHandles.get(id));

        try {
            ByteBuffer imgBuf = ioResourceToByteBuffer("background.png", 256 * 1024);
            int background = glCreateTextures(GL_TEXTURE_2D);
            glTextureParameteri(background, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTextureParameteri(background, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTextureParameteri(background, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTextureParameteri(background, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);
                ByteBuffer img = STBImage.stbi_load_from_memory(imgBuf, width, height, channels, 4);
                glTextureStorage2D(background, 1, GL_RGBA8, width.get(0), height.get(0));
                assert img != null;
                glTextureSubImage2D(background, 0, 0, 0, width.get(0), height.get(0), GL_RGBA, GL11C.GL_UNSIGNED_BYTE, img);
                STBImage.stbi_image_free(img);
            }
            textureHandles.put(background, ARBBindlessTexture.glGetTextureHandleARB(background));
            ARBBindlessTexture.glMakeTextureHandleResidentARB(textureHandles.get(background));
            this.background = NkImage.create();
            nk_image_id(background, this.background);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupContext() {
        ctx = NkContext.create();
        nk_init(ctx, allocator, null);

        cmds = NkBuffer.create();
        nk_buffer_init(cmds, allocator, 4 * 1024);
        window.setupNuklearContext(this.ctx);
    }

    private void setupRenderer() {
        shader = new NuklearShader();

        final Vector2i size = window.getWindowSize();
        final Matrix4f mat = new Matrix4f(2.0f / size.x, 0.0f, 0.0f, 0.0f,
                0.0f, -2.0f / size.y, 0.0f, 0.0f,
                0.0f, 0.0f, -1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f, 1.0f);
        shader.loadProjectionMatrix(mat);

        vao = glCreateVertexArrays();
        vbo = glCreateBuffers();
        ebo = glCreateBuffers();

        glEnableVertexArrayAttrib(vao, 0);
        glEnableVertexArrayAttrib(vao, 1);
        glEnableVertexArrayAttrib(vao, 2);

        glVertexArrayAttribBinding(vao, 0, 0);
        glVertexArrayAttribBinding(vao, 1, 0);
        glVertexArrayAttribBinding(vao, 2, 0);

        glVertexArrayAttribFormat(vao, 0, 2, GL_FLOAT, false, 0);
        glVertexArrayAttribFormat(vao, 1, 2, GL_FLOAT, false, 8);
        glVertexArrayAttribFormat(vao, 2, 4, GL_UNSIGNED_BYTE, true, 16);

        glVertexArrayElementBuffer(vao, ebo);
        glVertexArrayVertexBuffer(vao, 0, vbo, 0, 20);

        grey = NkColor.create();
        nk_rgb(128, 128, 128, grey);

        int flags = GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT;

        glNamedBufferStorage(vbo, 512 * 1024, flags);
        glNamedBufferStorage(ebo, 128 * 1024, flags);

        ByteBuffer vertexBuffer = glMapNamedBufferRange(vbo, 0, 512 * 1024, flags);
        ByteBuffer elementBuffer = glMapNamedBufferRange(ebo, 0, 128 * 1024, flags);

        convertConfig = NkConvertConfig.create()
                .vertex_layout(VERTEX_LAYOUT)
                .vertex_size(20)
                .vertex_alignment(4)
                .null_texture(null_texture)
                .circle_segment_count(22)
                .curve_segment_count(22)
                .arc_segment_count(22)
                .global_alpha(1.0f)
                .shape_AA(NK_ANTI_ALIASING_OFF)
                .line_AA(NK_ANTI_ALIASING_OFF);

        vbuf = NkBuffer.create();
        ebuf = NkBuffer.create();

        assert vertexBuffer != null && elementBuffer != null;
        nk_buffer_init_fixed(vbuf, vertexBuffer);
        nk_buffer_init_fixed(ebuf, elementBuffer);
    }

    private void initFont() {
        try {
            STBTTFontinfo fontInfo = STBTTFontinfo.create();
            STBTTPackedchar.Buffer cdata    = STBTTPackedchar.create(95);
            float scale, descent;
            int FONT_HEIGHT = 18;
            int BITMAP_W = 1024;
            int BITMAP_H = 1024;

            ttf = ioResourceToByteBuffer("FiraSans-Regular.ttf", 512 * 1024);

            try (MemoryStack stack = stackPush()) {
                stbtt_InitFont(fontInfo, ttf);
                scale = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);

                IntBuffer d = stack.mallocInt(1);
                stbtt_GetFontVMetrics(fontInfo, null, d, null);
                descent = d.get(0) * scale;

                ByteBuffer bitmap = memAlloc(BITMAP_W * BITMAP_H);

                STBTTPackContext pc = STBTTPackContext.mallocStack(stack);
                stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL);
                stbtt_PackSetOversampling(pc, 4, 4);
                stbtt_PackFontRange(pc, ttf, 0, FONT_HEIGHT, 32, cdata);
                stbtt_PackEnd(pc);

                ByteBuffer texture = memAlloc(BITMAP_W * BITMAP_H * 4);
                for (int i = 0; i < bitmap.capacity(); i++) {
                    texture.putInt((bitmap.get(i) << 24) | 0x00FFFFFF);
                }
                texture.flip();

                fontTextureID = glCreateTextures(GL_TEXTURE_2D);

                glTextureStorage2D(fontTextureID, 1, GL_RGBA8, BITMAP_W, BITMAP_H);

                glTextureSubImage2D(fontTextureID, 0, 0, 0, BITMAP_W, BITMAP_H, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, texture);

                glTextureParameteri(fontTextureID, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTextureParameteri(fontTextureID, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

                textureHandles.put(fontTextureID, ARBBindlessTexture.glGetTextureHandleARB(fontTextureID));
                ARBBindlessTexture.glMakeTextureHandleResidentARB(textureHandles.get(fontTextureID));

                memFree(texture);
                memFree(bitmap);
            }

            default_font = NkUserFont.create();

            default_font
                    .width((handle, h, text, len) -> {
                        float text_width = 0;
                        try (MemoryStack stack = stackPush()) {
                            IntBuffer unicode = stack.mallocInt(1);

                            int glyph_len = nnk_utf_decode(text, memAddress(unicode), len);
                            int text_len  = glyph_len;

                            if (glyph_len == 0) {
                                return 0;
                            }

                            IntBuffer advance = stack.mallocInt(1);
                            while (text_len <= len && glyph_len != 0) {
                                if (unicode.get(0) == NK_UTF_INVALID) {
                                    break;
                                }

                                /* query currently drawn glyph information */
                                stbtt_GetCodepointHMetrics(fontInfo, unicode.get(0), advance, null);
                                text_width += advance.get(0) * scale;

                                /* offset next glyph */
                                glyph_len = nnk_utf_decode(text + text_len, memAddress(unicode), len - text_len);
                                text_len += glyph_len;
                            }
                        }
                        return text_width;
                    })
                    .height(FONT_HEIGHT)
                    .query((handle, font_height, glyph, codepoint, next_codepoint) -> {
                        try (MemoryStack stack = stackPush()) {
                            FloatBuffer x = stack.floats(0.0f);
                            FloatBuffer y = stack.floats(0.0f);

                            STBTTAlignedQuad q       = STBTTAlignedQuad.mallocStack(stack);
                            IntBuffer        advance = stack.mallocInt(1);

                            stbtt_GetPackedQuad(cdata, BITMAP_W, BITMAP_H, codepoint - 32, x, y, q, false);
                            stbtt_GetCodepointHMetrics(fontInfo, codepoint, advance, null);

                            NkUserFontGlyph ufg = NkUserFontGlyph.create(glyph);

                            ufg.width(q.x1() - q.x0());
                            ufg.height(q.y1() - q.y0());
                            ufg.offset().set(q.x0(), q.y0() + (FONT_HEIGHT + descent));
                            ufg.xadvance(advance.get(0) * scale);
                            ufg.uv(0).set(q.s0(), q.t0());
                            ufg.uv(1).set(q.s1(), q.t1());
                        }
                    })
                    .texture(it -> it
                            .id(fontTextureID));

                    nk_style_set_font(ctx, default_font);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        nk_free(ctx);
        shader.dispose();
        glDeleteTextures(fontTextureID);
        glDeleteTextures(null_texture.texture().id());
        glDeleteTextures(background.handle().id());
        nk_buffer_free(ebuf);
        nk_buffer_free(vbuf);
        glUnmapNamedBuffer(vbo);
        glUnmapNamedBuffer(ebo);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
        nk_buffer_free(cmds);
        Objects.requireNonNull(convertConfig).free();
        Objects.requireNonNull(default_font.query()).free();
        Objects.requireNonNull(default_font.width()).free();
        Objects.requireNonNull(allocator.alloc()).free();
        Objects.requireNonNull(allocator.mfree()).free();
    }
}
