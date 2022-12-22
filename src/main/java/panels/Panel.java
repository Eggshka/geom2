package panels;

import io.github.humbleui.jwm.Event;
import io.github.humbleui.jwm.Window;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import misc.CoordinateSystem2i;

import java.util.function.Consumer;

import static app.Application.C_RAD_IN_PX;


/**
 * Класс панели
 */
public abstract class Panel implements Consumer<Event> {
    /**
     * отступ в пикселях
     */
    protected int padding;
    /**
     * переменная окна
     */
    protected final Window window;
    /**
     * флаг, нужно ли рисовать подложку
     */
    private final boolean drawBG;
    /**
     * цвет подложки
     */
    protected final int backgroundColor;

    /**
     * Конструктор панели
     *
     * @param window          окно
     * @param drawBG          нужно ли рисовать подложку
     * @param backgroundColor цвет фона
     * @param padding         отступы
     */
    public Panel(Window window, boolean drawBG, int backgroundColor, int padding) {
        this.window = window;
        this.drawBG = drawBG;
        this.backgroundColor = backgroundColor;
        this.padding = padding;
    }

    /**
     * Функция рисования, вызывающаяся извне
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        canvas.save();
        canvas.clipRect(windowCS.getRect());
        if (drawBG) {
            try (var paint = new Paint()) {
                paint.setColor(backgroundColor);
                canvas.drawRRect(windowCS.getRRect(C_RAD_IN_PX), paint);
            }
        }
        canvas.translate(windowCS.getMin().x, windowCS.getMin().y);
        paintImpl(canvas, windowCS);
        canvas.restore();
    }

    /**
     * Метод рисованияв конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public abstract void paintImpl(Canvas canvas, CoordinateSystem2i windowCS);

}