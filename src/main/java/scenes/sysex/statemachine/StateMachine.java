package scenes.sysex.statemachine;

import lombok.Getter;

public class StateMachine {
    @Getter
    private static final StateMachine instance = new StateMachine();

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
        switch (state) {
            case IDLE:
                break;
            case NEW:
                break;
            case SELECT:
                break;
        }
    }
}
