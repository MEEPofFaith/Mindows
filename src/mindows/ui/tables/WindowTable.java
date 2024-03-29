package mindows.ui.tables;

import arc.func.Cons;
import arc.input.KeyCode;
import arc.math.geom.Vec2;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Tmp;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

public class WindowTable extends Table{
    public Cons<Table> content, onClose;
    public TextureRegionDrawable icon;

    public WindowTable(String title, TextureRegionDrawable icon, Cons<Table> content){
        this(title, icon, content, t -> t.visible(() -> false));
    }

    public WindowTable(String title, TextureRegionDrawable icon, Cons<Table> content, Cons<Table> onClose){
        this.name = title;
        this.icon = icon;
        this.content = content;
        this.onClose = onClose;
        build();
    }

    public void build(){
        top();
        topBar();

        // window contents
        table(Styles.black5, t -> {
            content.get(t);
        }).grow();

        resizeButton();
    }

    public void topBar(){
        table(t -> {
            t.table(Tex.buttonEdge1, b -> {
                b.top().left();
                b.image(icon).size(20f).padLeft(15).top().left();
                b.pane(Styles.nonePane, p -> {
                    p.top().left();
                    p.labelWrap(name).padLeft(20).top().left().get().setAlignment(Align.topLeft);
                }).top().left().height(40f).growX().get().setScrollingDisabled(true, true);
            }).maxHeight(40f).grow();
            t.table(Tex.buttonEdge3, b -> {
                b.button(Icon.cancel, Styles.emptyi, () -> onClose.get(this));
            }).maxHeight(40f).width(80f).growY();

            t.touchable = Touchable.enabled;
            t.addListener(new InputListener(){
                float lastX, lastY;
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
                    Vec2 v = t.localToStageCoordinates(Tmp.v1.set(x, y));
                    lastX = v.x;
                    lastY = v.y;
                    t.toFront();
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    Vec2 v = t.localToStageCoordinates(Tmp.v1.set(x, y));

                    moveBy(v.x - lastX, v.y - lastY);
                    lastX = v.x;
                    lastY = v.y;
                }
            });
        }).top().height(40f).growX();
        row();
    }

    public void resizeButton(){
        row();
        table(Styles.black5, t -> {
            t.table().growX();
            t.table(Icon.resizeSmall, r -> {
                r.bottom().left();
                r.touchable = Touchable.enabled;
                r.addListener(new InputListener(){
                    float lastX, lastY;
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
                        Vec2 v = r.localToStageCoordinates(Tmp.v1.set(x, y));
                        lastX = v.x;
                        lastY = v.y;
                        return true;
                    }

                    @Override
                    public void touchDragged(InputEvent event, float x, float y, int pointer) {
                        Vec2 v = r.localToStageCoordinates(Tmp.v1.set(x, y));
                        float w = v.x - lastX;
                        float h = v.y - lastY;

                        // will softlock if initial size is smaller than minimum
                        // so don't do that!
                        if(getWidth() + w < 160f) w = 0;
                        if(getHeight() - h  < 160f) h = 0;
                        sizeBy(w, -h);
                        moveBy(0, h);
                        lastX = v.x;
                        lastY = v.y;
                    }
                });
            }).size(20f).left();
        }).height(20f).growX();
    }
}
