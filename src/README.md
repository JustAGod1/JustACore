# JustACore



JustACore is a core mod for minecraft. It provides you a lot of simple classes to perform your GuiScreen experience.

  - Center-aligned, auto-scaled picture from hard-drive in one line
  - Animated center-aligned, auto-scaled picture from hard-drive on hud in two lines
  - Animated, center-aligned, auto-scaled, dragable minecraft gui inside of another gui screen in three lines

# Examples!
Just a centered text at the center of the screen
```java
public class SimpleGui extends PichGui {
    public SimpleGui() {
        addOverlay(new CenteredTextOverlay(50, 50, "Hello, world!"));
    }
}
```
Centered text overlay that will appear at the center of the screen than it will start moving to a random direction.
```java
public class SimpleGui extends PichGui {
    public SimpleGui() {
        ScaledOverlay o = new CenteredTextOverlay(50, 50, "Hello, world!");
        addOverlay(o);
        o.addAnimator(new MovingAnimation(MathHelper.getRandomNormalizedVector());
    }
}
```
Centered text overlay that will appear at the center of the screen than it will start moving to a random direction. When you click on it, "awesome.png" will appered.
```java
public class SimpleGui extends PichGui {
    public SimpleGui() {
        ScaledOverlay o = new CenteredTextOverlay(50, 50, "Hello, world!");
        addOverlay(o);

        o.addAnimator(new MovingAnimation(MathHelper.getRandomNormalizedVector());

        o.mouseClickListeners.add(new MouseClickListener() {
            @Override
            public void onClick(double x, double y, ScaledOverlay overlay) {
                try {
                    overlay.getParent().addOverlay(new BufferedImageOverlay(x, y, 10, 10, ImageIO.read(new File("awesome.png"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
```
If you want to open your own PichGui, you has to use:
```java
new SimpleGui().open();
```
# Parents
Every scaled overlays have a parent. Parent is a just interface.
```java
public interface OverlayParent {

    void addOverlay(ScaledOverlay overlay);

    void removeOverlay(ScaledOverlay overlay);

    Collection<ScaledOverlay> getOverlays();

    void setOverlays(OverlaySet overlaySet);

    void addOverlays(Collection<ScaledOverlay> overlays);

    void appendOverlays(OverlaySet overlaySet);

    void moveUp(ScaledOverlay overlay);

    void moveDown(ScaledOverlay overlay);

    void moveToFront(ScaledOverlay overlay);

    void moveToBackground(ScaledOverlay overlay);

    double getScaledWidth();

    double getScaledHeight();

    double getScaledX();

    double getScaledY();

    void clear();
}
```

I wrote three implementations:
  - PichGui - GuiScreen that implements OverlayParent
  - PanelOverlay - ScaledOverlay that implements OverlayParent. You can use it like panel.
  - InGameOverlays - That class will render overlays at the minecraft hud.

Panel overlay usage example:
```java
public class SimpleGui extends PichGui {
    public SimpleGui() {
        PanelOverlay panelOverlay = new PanelOverlay(25, 25, 50, 50);
        addOverlay(panelOverlay);
        panelOverlay.addOverlay(new TextInputOverlay(50, 50, 30, 10));
    }
}
```

# Event listeners

There are four kinds of listeners in this mod:

  - Keyboard listener. Invoked when an Overlay in focus and key has been typed.
  - Mouse click listener. Invoked when an Overlay in focus and the mouse left key has been clicked.
  - Mouse drag listener. Invoked when mouse has been draged this Overlay.
  - Mouse hover listener. Invoked when cursor inside of this Overlay.

# Animation
Animator is just a very simple interface, that called every tick by Minecraft client render loop.
Code:
```java
public interface OverlayAnimator<T extends Overlay> {

    void init(T overlay);

    void update(T overlay);

    boolean isDead();
}
```
Ofcourse I wrote some kinds of animators.
If you want to add any animator to Overlay, you have to do this:
```java
ColorOverlay overlay = new ColorOverlay(50, 50, 4, 4, 1, 0.5, 0.2);
overlay.addAnimator(new MovingToAnimation(50, 30, 24));
```
If you want to add Queue of animators, you have to do this:
```java
QueuedAnimation.Builder<ScaledOverlay> builder = new QueuedAnimation.Builder<ScaledOverlay>();
builder.appendDelay(30).append(new FadeOutAnimation()).appendRemove();
overlay.addAnimator(builder.build());
```
# Scaling
Every ScaledOverlays have real and relative position and size. One unit of relative size is 1/100 of real size of parent. One unit of relative position is real position of parent plus 1/100 of its size.
You can *cancel* scaling:
```java
overlay.setScalePosition(false);
overlay.setScaleSize(false);
```
But I strongly dont recomend you to do that.

Also you can customize scaling:
```java
overlay.setScaleMode(ScaledOverlay.ScaleMode.HEIGHT_EQUAL_WIDTH);
overlay.setScaleMode(ScaledOverlay.ScaleMode.WIDTH_EQUAL_HEIGHT);
overlay.setScaleMode(ScaledOverlay.ScaleMode.DONT_SCALE_HEIGHT);
overlay.setScaleMode(ScaledOverlay.ScaleMode.DONT_SCALE_WIDTH);
overlay.setScaleMode(ScaledOverlay.ScaleMode.NORMAL);
```
# Transformation
You can add some transformations to ScaledOverlay. Transformator's code will invoked before rendering.
Example:
```java
overlay.transformations.add(new ColorTransformation(1, 0.2, 1, 1));
overlay.transformations.add(new Transformation() {
            @Override
            public void transform(ScaledOverlay scaledOverlay) {
                GL11.glScaled(10, 1, 1);
            }
        });
```
# Customization
You can write your own ScaledOverlay=). Just *override* ScaledOverlay.

Also my mod has __minecraft file chooser__.=)

