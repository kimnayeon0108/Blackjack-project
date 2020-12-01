package blackjack;

public class Hands {
    // initial two 카드 받음
    public int handsNum;
    public int bAmount;

    public String firstCard;
    public String secondCard;

    public int firstValue = 0;
    public int secondValue = 0;
    public int totalValue;

    public boolean pOver;
    public boolean pBlackjack;
    public boolean pAce;        // 핸드별로 ace 유무 여부
}
