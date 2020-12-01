package blackjack;

import java.util.Arrays;
import java.util.Collections;

public class Card {

    private String[] cardNum = {"a", "2", "3", "4", "5", "6", "7",
                                "8", "9", "10", "J", "Q", "K"};
    private String[] shape = {"♥", "♠", "♦", "♣"};
    private final int oneDeckNum = cardNum.length * shape.length;
    private String[] card = new String[oneDeckNum];

    public String[] sixDeckCard = new String[oneDeckNum * 6];
    public int[] valueArr = new int[sixDeckCard.length];        // 카드 값에 따른 value 배열 생성

    public final String ANSI_RESET = "\u001B[0m";
    public final String ANSI_RED = "\u001B[31m";

    public Card() {
        getOneDeckCard();
        shuffleCard();
        getCardValue(sixDeckCard);
    }

    // 한덱의 카드 생성
    private String[] getOneDeckCard() {
        int idx = 0;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < cardNum.length; j++) {
                card[idx] = cardNum[j] + shape[i];
                if (shape[i].equals("♥") || shape[i].equals("♦")) {
                    card[idx] = ANSI_RED + card[idx] + ANSI_RESET;
                }
                idx++;
            }
        }
        return card;
    }

    private String[] shuffleCard() {
        for (int i = 0; i < sixDeckCard.length; i++) {
            sixDeckCard[i] = card[i % oneDeckNum];
        }
        Collections.shuffle(Arrays.asList(sixDeckCard));

        return sixDeckCard;
    }

    private int[] getCardValue(String[] sixDeckCard) {

        //Todo: 이부분 너무 하드코딩 인듯 ㅠㅠ 나중에 다시 생각해보기
        for (int i = 0; i < sixDeckCard.length; i++) {
            switch (sixDeckCard[i]) {
                case "\u001B[31ma♥\u001B[0m":
                case "a♠":
                case "\u001B[31ma♦\u001B[0m":
                case "a♣":
                    valueArr[i] = 1;
                    break;
                case "\u001B[31m2♥\u001B[0m":
                case "2♠":
                case "\u001B[31m2♦\u001B[0m":
                case "2♣":
                    valueArr[i] = 2;
                    break;
                case "\u001B[31m3♥\u001B[0m":
                case "3♠":
                case "\u001B[31m3♦\u001B[0m":
                case "3♣":
                    valueArr[i] = 3;
                    break;
                case "\u001B[31m4♥\u001B[0m":
                case "4♠":
                case "\u001B[31m4♦\u001B[0m":
                case "4♣":
                    valueArr[i] = 4;
                    break;
                case "\u001B[31m5♥\u001B[0m":
                case "5♠":
                case "\u001B[31m5♦\u001B[0m":
                case "5♣":
                    valueArr[i] = 5;
                    break;
                case "\u001B[31m6♥\u001B[0m":
                case "6♠":
                case "\u001B[31m6♦\u001B[0m":
                case "6♣":
                    valueArr[i] = 6;
                    break;
                case "\u001B[31m7♥\u001B[0m":
                case "7♠":
                case "\u001B[31m7♦\u001B[0m":
                case "7♣":
                    valueArr[i] = 7;
                    break;
                case "\u001B[31m8♥\u001B[0m":
                case "8♠":
                case "\u001B[31m8♦\u001B[0m":
                case "8♣":
                    valueArr[i] = 8;
                    break;
                case "\u001B[31m9♥\u001B[0m":
                case "9♠":
                case "\u001B[31m9♦\u001B[0m":
                case "9♣":
                    valueArr[i] = 9;
                    break;
                case "\u001B[31m10♥\u001B[0m":
                case "10♠":
                case "\u001B[31m10♦\u001B[0m":
                case "10♣":
                case "\u001B[31mJ♥\u001B[0m":
                case "J♠":
                case "\u001B[31mJ♦\u001B[0m":
                case "J♣":
                case "\u001B[31mQ♥\u001B[0m":
                case "Q♠":
                case "\u001B[31mQ♦\u001B[0m":
                case "Q♣":
                case "\u001B[31mK♥\u001B[0m":
                case "K♠":
                case "\u001B[31mK♦\u001B[0m":
                case "K♣":
                    valueArr[i] = 10;
                    break;
            }
        }
        return valueArr;
    }

}

