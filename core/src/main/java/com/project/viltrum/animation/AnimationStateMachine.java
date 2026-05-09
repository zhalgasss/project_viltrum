package com.project.viltrum.animation;

import com.project.viltrum.entities.AnimationState;

public class AnimationStateMachine {
    private AnimationState state = AnimationState.IDLE;
    private float stateTime = 0;
    private float lockTimer = 0;

    public void update(float delta) {
        stateTime += delta;

        if (lockTimer > 0 && lockTimer < Float.MAX_VALUE) {
            lockTimer -= delta;

            if (lockTimer <= 0) {
                play(AnimationState.IDLE);
            }
        }
    }

    public void play(AnimationState nextState) {
        if (isLocked() || state == nextState) {
            return;
        }

        changeState(nextState);
    }

    public void playLocked(AnimationState nextState, float duration) {
        changeState(nextState);
        lockTimer = duration;
    }

    public void force(AnimationState nextState) {
        changeState(nextState);
        lockTimer = 0;
    }

    public void setLocomotion(boolean moving) {
        if (!isLocked()) {
            play(moving ? AnimationState.WALK : AnimationState.IDLE);
        }
    }

    public boolean isLocked() {
        return lockTimer > 0;
    }

    public boolean isDeathState() {
        return state == AnimationState.DEATH;
    }

    public AnimationState getState() {
        return state;
    }

    public float getStateTime() {
        return stateTime;
    }

    private void changeState(AnimationState nextState) {
        if (state == nextState) {
            return;
        }

        state = nextState;
        stateTime = 0;
    }
}
