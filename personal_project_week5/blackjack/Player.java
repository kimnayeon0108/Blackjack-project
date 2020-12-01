package blackjack;

public class Player {
    public String name;
    public int money;
    public int pChips;

    public Player(String name){
        this.name = name;
        this.pChips = 0;
        this.money = 3_000_000;    // player 기본적으로 삼백만원 생성
    }
}
