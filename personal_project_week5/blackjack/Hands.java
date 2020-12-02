package blackjack;

public class Hands {
    // initial two 카드 받음
    public int handsNum;
    public int bAmount;

    public String firstCard;
    public String secondCard;

    public int firstValue;
    public int secondValue;
    public int totalValue;

    public boolean pOver = false;
    public boolean pBlackjack = false;
    public boolean pAce = false;        // 핸드별로 ace 유무 여부
}
