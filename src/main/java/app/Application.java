package app;

import io.github.humbleui.jwm.*;
import io.github.humbleui.jwm.skija.EventFrameSkija;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.RRect;
import io.github.humbleui.skija.Surface;
import misc.CoordinateSystem2i;
import misc.Misc;

import java.io.File;
import java.util.function.Consumer;

import static app.Colors.APP_BACKGROUND_COLOR;

/**
 * класс окна приложения
 */
public class Application implements Consumer<Event> {
    /**
 * радиус скругления элементов
 */
public static final int C_RAD_IN_PX = 4;
    /**
     * окно приложения
     */
    private final Window window;

    /**
     * конструктор класса приложение
     */
    public Application() {
        window = App.makeWindow();
        window.setEventListener(this);
        window.setVisible(true);
        window.setTitle("Java 2D");
        window.setWindowSize(900, 900);
        window.setWindowPosition(100, 100);
        switch (Platform.CURRENT) {
            case WINDOWS -> window.setIcon(new File("src/main/resources/windows.ico"));
            case MACOS -> window.setIcon(new File("src/main/resources/macos.icns"));
        }
        String[] layerNames = new String[]{
                "LayerGLSkija", "LayerRasterSkija"
        };
        for (String layerName : layerNames) {
            String className = "io.github.humbleui.jwm.skija." + layerName;
            try {
                Layer layer = (Layer) Class.forName(className).getDeclaredConstructor().newInstance();
                window.setLayer(layer);
                break;
            } catch (Exception e) {
                System.out.println("Ошибка создания слоя " + className);
            }
        }

        if (window._layer == null)
            throw new RuntimeException("Нет доступных слоёв для создания");
    }

    /**
     * Обработчик событий
     * @param e the input argument
     */
    @Override
    public void accept(Event e) {
        if (e instanceof EventWindowClose) {
            App.terminate();
        }else if (e instanceof EventWindowCloseRequest) {
            window.close();
        }
        else if (e instanceof EventFrameSkija ee) {
            Surface s = ee.getSurface();
            paint(s.getCanvas(), new CoordinateSystem2i(s.getWidth()/3, s.getHeight()/3, s.getWidth()/3, s.getHeight()/3));
        }
    }
    /**
     * Рисование
     *
     * @param canvas   низкоуровневый инструмент рисования примитивов от Skija
     * @param windowCS СК окна
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // запоминаем изменения (пока что там просто заливка цветом)
        canvas.save();
        // очищаем канвас
        canvas.clear(APP_BACKGROUND_COLOR);
        // создаём кисть
        Paint paint = new Paint();
        // задаём цвет рисования
        paint.setColor(Misc.getColor(100, 255, 255, 255));
        // рисуем квадрат
        canvas.drawRRect(windowCS.getRRect(4), paint);
        // восстанавливаем состояние канваса
        canvas.restore();
    }
}