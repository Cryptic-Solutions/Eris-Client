package me.spec.eris.ui.fonts;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class TTFFontRenderer {
    private final boolean antiAlias;
    private Font font;
    private boolean fractionalMetrics;
    private CharacterData[] regularData;
    private CharacterData[] boldData;
    private CharacterData[] italicsData;
    private int[] colorCodes;
    private static final int MARGIN = 4;
    private static final char COLOR_INVOKER = '\u00a7';
    private static int RANDOM_OFFSET;

    public TTFFontRenderer(final ExecutorService executorService, final ConcurrentLinkedQueue<TextureData> textureQueue, final Font font) {
        this(executorService, textureQueue, font, 256);
    }

    public TTFFontRenderer(final ExecutorService executorService, final ConcurrentLinkedQueue<TextureData> textureQueue, final Font font, final int characterCount) {
        this(executorService, textureQueue, font, characterCount, true);
    }

    public TTFFontRenderer(final ExecutorService executorService, final ConcurrentLinkedQueue<TextureData> textureQueue, final Font font, final boolean antiAlias) {
        this(executorService, textureQueue, font, 256, antiAlias);
    }

    public TTFFontRenderer(final ExecutorService executorService, final ConcurrentLinkedQueue<TextureData> textureQueue, final Font font, final int characterCount, final boolean antiAlias) {
        this.fractionalMetrics = false;
        this.colorCodes = new int[32];
        this.font = font;
        this.fractionalMetrics = true;
        this.antiAlias = antiAlias;
        final int[] regularTexturesIds = new int[characterCount];
        final int[] boldTexturesIds = new int[characterCount];
        final int[] italicTexturesIds = new int[characterCount];
        for (int i = 0; i < characterCount; ++i) {
            regularTexturesIds[i] = GL11.glGenTextures();
            boldTexturesIds[i] = GL11.glGenTextures();
            italicTexturesIds[i] = GL11.glGenTextures();
        }
        executorService.execute(() -> this.regularData = this.setup(new CharacterData[characterCount], regularTexturesIds, textureQueue, 0));
        executorService.execute(() -> this.boldData = this.setup(new CharacterData[characterCount], boldTexturesIds, textureQueue, 1));
        executorService.execute(() -> this.italicsData = this.setup(new CharacterData[characterCount], italicTexturesIds, textureQueue, 2));
    }

    private CharacterData[] setup(final CharacterData[] characterData, final int[] texturesIds, final ConcurrentLinkedQueue<TextureData> textureQueue, final int type) {
        this.generateColors();
        final Font font = this.font.deriveFont(type);
        final BufferedImage utilityImage = new BufferedImage(1, 1, 2);
        final Graphics2D utilityGraphics = (Graphics2D) utilityImage.getGraphics();
        utilityGraphics.setFont(font);
        final FontMetrics fontMetrics = utilityGraphics.getFontMetrics();
        for (int index = 0; index < characterData.length; ++index) {
            final char character = (char) index;
            final Rectangle2D characterBounds = fontMetrics.getStringBounds(character + "", utilityGraphics);
            final float width = (float) characterBounds.getWidth() + 8.0f;
            final float height = (float) characterBounds.getHeight();
            final BufferedImage characterImage = new BufferedImage(MathHelper.ceiling_double_int(width), MathHelper.ceiling_double_int(height), 2);
            final Graphics2D graphics = (Graphics2D) characterImage.getGraphics();
            graphics.setFont(font);
            graphics.setColor(new Color(255, 255, 255, 0));
            graphics.fillRect(0, 0, characterImage.getWidth(), characterImage.getHeight());
            graphics.setColor(Color.WHITE);
            if (this.antiAlias) {
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            }
            graphics.drawString(character + "", 4, fontMetrics.getAscent());
            final int textureId = texturesIds[index];
            this.createTexture(textureId, characterImage, textureQueue);
            characterData[index] = new CharacterData(character, (float) characterImage.getWidth(), (float) characterImage.getHeight(), textureId);
        }
        return characterData;
    }

    private void createTexture(final int textureId, final BufferedImage image, final ConcurrentLinkedQueue<TextureData> textureQueue) {
        final int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                final int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) (pixel >> 16 & 0xFF));
                buffer.put((byte) (pixel >> 8 & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) (pixel >> 24 & 0xFF));
            }
        }
        buffer.flip();
        textureQueue.add(new TextureData(textureId, image.getWidth(), image.getHeight(), buffer));
    }

    public int drawString(final String text, final float x, float y, final int color) {
        return this.renderString(text, x, y, color, false);
    }

    public void drawCenteredString(final String text, final float x, final float y, final int color) {
        final float width = this.getStringWidth(text) / 2.0f;
        this.renderString(text, x - width, y, color, false);
    }

    public void drawStringWithShadow(final String text, final float x, final double d, final int color) {
        GL11.glTranslated(0.5, 0.5, 0.0);
        this.renderString(text, x, d, color, true);
        GL11.glTranslated(-0.5, -0.5, 0.0);
        this.renderString(text, x, d, color, false);
    }

    private int renderString(final String text, float x, double d, final int color, final boolean shadow) {
        if (text == null) return 0;
        if (text == "" || text.length() == 0) {
            return 0;
        }
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 1.0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        x -= 2.0f;
        d -= 2.0f;
        x += 0.5f;
        d += 0.5f;
        x *= 2.0f;
        d *= 2.0f;
        CharacterData[] characterData = this.regularData;
        boolean underlined = false;
        boolean strikethrough = false;
        boolean obfuscated = false;
        final int length = text.length();
        final double multiplier = 255.0 * (shadow ? 4 : 1);
        final Color c = new Color(color);
        GL11.glColor4d(c.getRed() / multiplier, c.getGreen() / multiplier, c.getBlue() / multiplier, (color >> 24 & 0xFF) / 255.0);
        for (int i = 0; i < length; ++i) {
            char character = text.charAt(i);
            final char previous = (i > 0) ? text.charAt(i - 1) : '.';
            if (previous != '\u00a7') {
                if (character == '\u00a7' && i < length && text.length() > 2) {
                    int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                    if (index < 16) {
                        obfuscated = false;
                        strikethrough = false;
                        underlined = false;
                        characterData = this.regularData;
                        if (index < 0 || index > 15) {
                            index = 15;
                        }
                        if (shadow) {
                            index += 16;
                        }
                        final int textColor = this.colorCodes[index];
                        GL11.glColor4d((textColor >> 16) / 255.0, (textColor >> 8 & 0xFF) / 255.0, (textColor & 0xFF) / 255.0, (color >> 24 & 0xFF) / 255.0);
                    } else if (index == 16) {
                        obfuscated = true;
                    } else if (index == 17) {
                        characterData = this.boldData;
                    } else if (index == 18) {
                        strikethrough = true;
                    } else if (index == 19) {
                        underlined = true;
                    } else if (index == 20) {
                        characterData = this.italicsData;
                    } else if (index == 21) {
                        obfuscated = false;
                        strikethrough = false;
                        underlined = false;
                        characterData = this.regularData;
                        GL11.glColor4d(1.0 * (shadow ? 0.25 : 1.0), 1.0 * (shadow ? 0.25 : 1.0), 1.0 * (shadow ? 0.25 : 1.0), (color >> 24 & 0xFF) / 255.0);
                    }
                } else if (character <= '\u00ff') {
                    if (obfuscated) {
                        character += (char) TTFFontRenderer.RANDOM_OFFSET;
                    }
                    this.drawChar(character, characterData, x, d);
                    final CharacterData charData = characterData[character];
                    if (strikethrough) {
                        this.drawLine(new Vector2f(0.0f, charData.height / 2.0f), new Vector2f(charData.width, charData.height / 2.0f), 3.0f);
                    }
                    if (underlined) {
                        this.drawLine(new Vector2f(0.0f, charData.height - 15.0f), new Vector2f(charData.width, charData.height - 15.0f), 3.0f);
                    }
                    x += charData.width - 8.0f;
                }
            }
        }
        GL11.glPopMatrix();
        GlStateManager.disableBlend();
        GlStateManager.bindTexture(0);
        GL11.glColor3f(1F, 1F, 1F);
        return (int) x;
    }

    public float getStringWidth(final String text) {
        float width = 0.0f;
        CharacterData[] characterData = this.regularData;
        for (int length = text.length(), i = 0; i < length; ++i) {
            final char character = text.charAt(i);
            final char previous = (i > 0) ? text.charAt(i - 1) : '.';
            if (previous != '\u00a7') {
                if (character == '\u00a7' && i < length) {
                    try {
                        final int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                        if (index == 17) {
                            characterData = this.boldData;
                        } else if (index == 20) {
                            characterData = this.italicsData;
                        } else if (index == 21) {
                            characterData = this.regularData;
                        }
                    } catch (Exception e) {

                    }
                } else if (character <= '\u00ff') {
                    final CharacterData charData = characterData[character];
                    width += (charData.width - 8.0f) / 2.0f;
                }
            }
        }
        return width + 2.0f;
    }

    public float getHeight(final String text) {
        float height = 0.0f;
        CharacterData[] characterData = this.regularData;
        for (int length = text.length(), i = 0; i < length; ++i) {
            final char character = text.charAt(i);
            final char previous = (i > 0) ? text.charAt(i - 1) : '.';
            if (previous != '\u00a7') {
                if (character == '\u00a7' && i < length) {
                    final int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                    if (index == 17) {
                        characterData = this.boldData;
                    } else if (index == 20) {
                        characterData = this.italicsData;
                    } else if (index == 21) {
                        characterData = this.regularData;
                    }
                } else if (character <= '\u00ff') {
                    final CharacterData charData = characterData[character];
                    height = Math.max(height, charData.height);
                }
            }
        }
        return height / 2.0f - 2.0f;
    }

    private void drawChar(final char character, final CharacterData[] characterData, final float x, final double d) {
        final CharacterData charData = characterData[character];
        charData.bind();
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2d((double) x, (double) d);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2d((double) x, (double) (d + charData.height));
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2d((double) (x + charData.width), (double) (d + charData.height));
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2d((double) (x + charData.width), (double) d);
        GL11.glEnd();
    }

    private void drawLine(final Vector2f start, final Vector2f end, final float width) {
        GL11.glDisable(3553);
        GL11.glLineWidth(width);
        GL11.glBegin(1);
        GL11.glVertex2f(start.x, start.y);
        GL11.glVertex2f(end.x, end.y);
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    private void generateColors() {
        for (int i = 0; i < 32; ++i) {
            final int thingy = (i >> 3 & 0x1) * 85;
            int red = (i >> 2 & 0x1) * 170 + thingy;
            int green = (i >> 1 & 0x1) * 170 + thingy;
            int blue = (i >> 0 & 0x1) * 170 + thingy;
            if (i == 6) {
                red += 85;
            }
            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCodes[i] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF));
        }
    }

    static {
        TTFFontRenderer.RANDOM_OFFSET = 1;
    }

    class CharacterData {
        public char character;
        public float width;
        public float height;
        private int textureId;

        public CharacterData(final char character, final float width, final float height, final int textureId) {
            this.character = character;
            this.width = width;
            this.height = height;
            this.textureId = textureId;
        }

        public void bind() {
            GL11.glBindTexture(3553, this.textureId);
        }
    }
}
