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
        handleStateChange(state);
    }

    public void setState(States state) {
        this.state = state;
        handleStateChange(state);
    }

    private void handleStateChange(States state) {
        if (actionsPane != null) {
            actionsPane.hideAllButtons();

            switch (state) {
                case IDLE:
                    actionsPane.showButton("New", true);
                    break;
                case ADD:
                    actionsPane.showButton("New", true);
                    actionsPane.showButton("Add", true);
                    actionsPane.showButton("Execute", true);
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
