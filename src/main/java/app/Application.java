package app;

import io.github.humbleui.jwm.*;

import java.util.function.Consumer;

public class Application implements Consumer<Event> {

    private final Window window;

    public Application() {
        window = App.makeWindow();
        window.setEventListener(this);
        window.setVisible(true);
    }

    @Override
    public void accept(Event e) {
        if (e instanceof EventWindowClose) {
            App.terminate();
        }else if (e instanceof EventWindowCloseRequest) {
            window.close();
        }
    }
}