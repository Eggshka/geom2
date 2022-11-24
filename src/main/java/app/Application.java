package app;

import io.github.humbleui.jwm.*;

import java.util.function.Consumer;

/**
 * класс окна приложения
 */
public class Application implements Consumer<Event> {
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
    }
}