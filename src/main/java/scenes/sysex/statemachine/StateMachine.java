package scenes.sysex.statemachine;

import lombok.Getter;
import lombok.Setter;
import scenes.SceneActionsPane;

public class StateMachine {
    @Getter
    private static final StateMachine instance = new StateMachine();

    @Setter
    private SceneActionsPane actionsPane;

    private States state;
    private StateMachine() {
        state = States.IDLE;
        handleStateChange(state, false);
    }

    private StateMachine(boolean executeOn) {
        this();

        state = States.IDLE;
        handleStateChange(state, executeOn);
    }

    public void setState(States state, boolean executeOn) {
        this.state = state;
        handleStateChange(state, executeOn);
    }

    private void handleStateChange(States state, boolean executeOn) {
        if (actionsPane != null) {
            actionsPane.hideAllButtons();
            if (executeOn) {
                actionsPane.showButton("Execute", true);
            }

            switch (state) {
                case IDLE:
                    actionsPane.showButton("New", true);
                    break;
                case ADD:
                    actionsPane.showButton("New", true);
                    actionsPane.showButton("Add", true);
                    break;
                case UPDATE:
                    actionsPane.showButton("New", true);
                    actionsPane.showButton("Delete", true);
                    actionsPane.showButton("Update", true);
                    actionsPane.showButton("Execute", true);
                    break;
                case NEW:
                    actionsPane.showButton("New", true);
                    break;
                case SELECT:
                    actionsPane.showButton("New", true);
                    actionsPane.showButton("Delete", true);
                    actionsPane.showButton("Execute", true);
                    break;
            }
        }
    }
}
