package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class creditCard implements Serializable {
    private int number;
    private int amount;

    public creditCard(int number, int amount) {
        this.number = number;
        this.amount = amount;
    }

    public int getNumber() {
        return number;
    }

    public int getAmount() {
        return amount;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
