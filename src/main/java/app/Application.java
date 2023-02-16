package app;

import controls.InputFactory;
import controls.Label;
import dialogs.PanelInfo;
import dialogs.PanelSelectFile;
import io.github.humbleui.jwm.*;
import io.github.humbleui.jwm.skija.EventFrameSkija;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.RRect;
import io.github.humbleui.skija.Surface;
import misc.CoordinateSystem2i;
import misc.Misc;
import panels.PanelControl;
import panels.PanelHelp;
import panels.PanelLog;
import panels.PanelRendering;

import java.io.File;
import java.util.function.Consumer;

import static app.Colors.*;

/**
 * класс окна приложения
 */
public class Application implements Consumer<Event> {
    /**
 * радиус скругления элементов
 */
public static final int C_RAD_IN_PX = 4;
    /**
     * панель легенды
     */
    private final PanelHelp panelHelp;
    /**
     * панель курсора мыши
     */
    private final PanelControl panelControl;
    /**
     * панель рисования
     */
    private final PanelRendering panelRendering;
    /**
     * панель событий
     */
    private final PanelLog panelLog;
    /**
     * Первый заголовок
     */
    private final Label label;
    /**
     * Первый заголовок
     */
    private final Label label2;
    /**
     * Первый заголовок
     */
    private final Label label3;
    /**
     * отступы панелей
     */
    public static final int PANEL_PADDING = 5;
    /**
     * окно приложения
     */

    private final Window window;
    /**
     * Режимы работы приложения
     */
    public enum Mode {
        /**
         * Основной режим работы
         */
        WORK,
        /**
         * Окно информации
         */
        INFO,
        /**
         * работа с файлами
         */
        FILE
    }
    /**
     * Текущий режим(по умолчанию рабочий)
     */
    public static Mode currentMode = Mode.WORK;
    /**
     * Панель информации
     */
    private final PanelInfo panelInfo;
    /**
     * кнопка изменений: у мака - это `Command`, у windows - `Ctrl`
     */
    public static final KeyModifier MODIFIER = Platform.CURRENT == Platform.MACOS ? KeyModifier.MAC_COMMAND : KeyModifier.CONTROL;
    /**
     * флаг того, что окно развёрнуто на весь экран
     */
    private boolean maximizedWindow;
    /**
     * Панель выбора файла
     */
    private final PanelSelectFile panelSelectFile;
    /**
     * конструктор класса приложение
     */
    public Application() {
        window = App.makeWindow();
        // создаём панель рисования
        panelRendering = new PanelRendering(
                window, true, PANEL_BACKGROUND_COLOR, PANEL_PADDING, 5, 3, 0, 0,
                3, 2
        );
        // создаём панель управления
        panelControl = new PanelControl(
                window, true, PANEL_BACKGROUND_COLOR, PANEL_PADDING, 5, 3, 3, 0,
                2, 2
        );
        // создаём панель лога
        panelLog = new PanelLog(
                window, true, PANEL_BACKGROUND_COLOR, PANEL_PADDING, 5, 3, 0, 2,
                3, 1
        );
        // создаём панель помощи
        panelHelp = new PanelHelp(
                window, true, PANEL_BACKGROUND_COLOR, PANEL_PADDING, 5, 3, 3, 2,
                2, 1
        );
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

        label = new Label(window, true, PANEL_BACKGROUND_COLOR, PANEL_PADDING,
                4, 4, 1, 1, 1, 1, "Привет, мир!", true, true);
        // создаём второй заголовок
        label2 = new Label(window, true, PANEL_BACKGROUND_COLOR, PANEL_PADDING,
                4, 4, 0, 3, 1, 1, "Второй заголовок", true, true);

        // создаём третий заголовок
        label3 = new Label(window, true, PANEL_BACKGROUND_COLOR, PANEL_PADDING,
                4, 4, 2, 0, 1, 1, "Это тоже заголовок", true, true);

        if (window._layer == null)
            throw new RuntimeException("Нет доступных слоёв для создания");
        // панель информации
        panelInfo = new PanelInfo(window, true, DIALOG_BACKGROUND_COLOR, PANEL_PADDING);
        // Панель выбора файла
        panelSelectFile = new PanelSelectFile(window, true, DIALOG_BACKGROUND_COLOR, PANEL_PADDING);
    }


    /**
     * Обработчик событий
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {
        // если событие - это закрытие окна
        if (e instanceof EventWindowClose) {
            // завершаем работу приложения
            App.terminate();
        } else if (e instanceof EventWindowCloseRequest) {
            window.close();
        } else if (e instanceof EventFrame) {
            // запускаем рисование кадра
            window.requestFrame();
        } else if (e instanceof EventFrameSkija ee) {
            // получаем поверхность рисования
            Surface s = ee.getSurface();
            // очищаем её канвас заданным цветом
            paint(s.getCanvas(), new CoordinateSystem2i(s.getWidth(), s.getHeight()));
        }// кнопки клавиатуры
        else if (e instanceof EventKey eventKey) {
            // кнопка нажата с Ctrl
            if (eventKey.isPressed()) {
                if (eventKey.isModifierDown(MODIFIER))
                    // разбираем, какую именно кнопку нажали
                    switch (eventKey.getKey()) {
                        case W -> window.close();
                        case H -> window.minimize();
                        case S -> PanelRendering.save();
                        case O -> PanelRendering.load();
                        case DIGIT1 -> {
                            if (maximizedWindow)
                                window.restore();
                            else
                                window.maximize();
                            maximizedWindow = !maximizedWindow;
                        }
                        case DIGIT2 -> window.setOpacity(window.getOpacity() == 1f ? 0.5f : 1f);
                    }
                else
                    switch (eventKey.getKey()) {
                        case ESCAPE -> {
                            // если сейчас основной режим
                            if (currentMode.equals(Mode.WORK)) {
                                // закрываем окно
                                window.close();
                                // завершаем обработку, иначе уже разрушенный контекст
                                // будет передан панелям
                                return;
                            } else if (currentMode.equals(Mode.INFO)) {
                                currentMode = Mode.WORK;
                            }
                        }
                        case TAB -> InputFactory.nextTab();
                    }
            }
        }
        switch (currentMode) {
            case INFO -> panelInfo.accept(e);
            case FILE -> panelSelectFile.accept(e);
            case WORK -> {
                // передаём события на обработку панелям
                panelControl.accept(e);
                panelRendering.accept(e);
                panelLog.accept(e);
            }
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
        // рисуем панели
        panelRendering.paint(canvas, windowCS);
        panelControl.paint(canvas, windowCS);
        panelLog.paint(canvas, windowCS);
        panelHelp.paint(canvas, windowCS);
        // рисуем диалоги
        switch (currentMode) {
            case INFO -> panelInfo.paint(canvas, windowCS);
            case FILE -> panelSelectFile.paint(canvas, windowCS);
        }
        canvas.restore();
    }
}