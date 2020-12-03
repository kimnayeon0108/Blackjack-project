package blackjack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Table implements Runnable {
    private Scanner s = new Scanner(System.in);
    private int buyingChips;
    private int numOfHands;
    private final int MAX_HANDS = 3;

    private Player p;
    private Card c;
    private Dealer d = new Dealer();
    private Hands[] handsArr;

    private int oneMoreCard = 0;        // 카드 한장씩 받을때마다 1씩 증가
    private int over = 0;    // 핸드 별로 오버시 1씩 커짐
    private boolean end;

    private int dividedCardNum = numOfHands * 2 + 1;    // 처음에 나눠주는 카드 수

    private DecimalFormat formatter = new DecimalFormat("###,###,###");

    public Table() {

        // Table 객체 생성시 플레이어 입력받기, 칩스 구매 메소드 실행
        initPlayer();
        buyChips();
        c = new Card();     // Card 객체 생성과 동시에, 카드 셔플 진행 완료
    }

    private void initPlayer() {
        System.out.println("사용자의 이름을 입력해주세요.");
        String getName = s.next();
        p = new Player(getName);
        System.out.println("사용자가 생성되었습니다. \n\t이름 :" + getName
                + "\t현금 : " + formatter.format(p.money) + "\t칩스 잔액 : " + p.pChips);
    }

    private void buyChips() {

        System.out.println();
        System.out.println("구매를 원하는 칩스의 금액을 입력해주세요.");
        buyingChips = s.nextInt();
        if (buyingChips % 10000 != 0) {
            System.out.println("만원 단위로만 구매가 가능합니다.");
            buyChips();
        }
        if (buyingChips > p.money) {
            System.out.println("현금이 부족합니다. 금액을 다시 입력해주세요.");
            buyChips();
        } else {
            p.money = p.money - buyingChips;
            p.pChips = p.pChips + buyingChips;
            d.dChips = d.dChips - buyingChips;

            System.out.println("칩스 " + formatter.format(buyingChips) + "이 생성되었습니다.");
            System.out.println("칩스 잔액 : " + formatter.format(p.pChips));
            System.out.println("딜러 보유 칩스 금액 : " + formatter.format(d.dChips));
        }
    }

    private void startGame() {

        // 핸드 수 지정, 베팅 금액 설정
        chooseNumOfHands();
        getHands(numOfHands);   // 받은 hands 수 만큼 array 생성

        for (int w = 0; w < handsArr.length; w++) {

            betChips(w);
        }
    }

    private int chooseNumOfHands() {
        System.out.println("\n플레이하고 싶은 Hand 수를 입력하세요. (1-3)");
        this.numOfHands = s.nextInt();

        // Three hands 이상 선택시
        if (numOfHands > MAX_HANDS || numOfHands < 1) {
            System.out.println("Three Hands 까지만 베팅이 가능합니다.\n다시 입력해 주세요.");
            chooseNumOfHands();
        }

        // 보유 칩스 금액이 선택한 hands 수보다 적을 시
        if (p.pChips < numOfHands * 10000) {
            System.out.println("칩스 보유량이 부족합니다.\n칩스를 구매하고 싶으면 1, " +
                    "Hands 수를 다시 입력하려면 2를 입력하세요.");
            int input = s.nextInt();
            if (input == 1) {
                buyChips();
                chooseNumOfHands();
            }
            if (input == 2) {
                chooseNumOfHands();
            }
        }
        return numOfHands;
    }

    private Hands[] getHands(int numOfHands) {

        // player 가 지정한 hands 수 만큼 hands 객체 생성해서 배열에 값 담기
        handsArr = new Hands[numOfHands];
        for (int i = 0; i < handsArr.length; i++) {
            handsArr[i] = new Hands();
            handsArr[i].handsNum = i + 1;
        }
        return handsArr;
    }

    private void betChips(int w) {

        boolean okay = true;

        while (okay) {
            System.out.printf("===== Hands %d 베팅할 금액을 입력하세요. =====\n", handsArr[w].handsNum);
            handsArr[w].bAmount = s.nextInt();

            // 만원 단위로 입력하지 않은 경우
            if (handsArr[w].bAmount % 10000 != 0) {
                System.out.println("만원 단위로 베팅이 가능합니다. 다시 입력해주세요.");
                continue;
            }

            // 맥시멈 금액 넘은 경우
            if (handsArr[w].bAmount > 100000) {
                System.out.println("maximum 10만원까지 베팅 가능합니다. 다시 입력해주세요.");
                continue;
            }

            // 보유 칩스량이 부족한 경우
            if (handsArr[w].bAmount > p.pChips) {
                System.out.println("보유 칩스량이 부족합니다. \n칩스를 구매하고 싶으면 1," +
                        " 베팅금액을 다시 입력하려면 2를 입력하세요.");
                int input2 = s.nextInt();
                if (input2 == 1) {
                    buyChips();
                    continue;
                }
                if (input2 == 2) {
                    continue;
                }
            } else {
                p.pChips = p.pChips - handsArr[w].bAmount;
                okay = false;
            }
        }
    }

    private void divideCards(int numOfHands) {
        // 나눠줄 카드 배열에 담기
        for (int i = 0; i < numOfHands; i++) {
            handsArr[i].firstCard = c.sixDeckCard[i];
        }

        d.firstCard = c.sixDeckCard[numOfHands];

        for (int j = 0; j < numOfHands; j++) {
            handsArr[j].secondCard = c.sixDeckCard[numOfHands + 1 + j];
        }

        handsValue(numOfHands);
        printDividedCards();
    }

    private void handsValue(int numOfHands) {
        // 핸드별로 value 얻기

        for (int i = 0; i < numOfHands; i++) {
            handsArr[i].firstValue = c.valueArr[i];
        }
        d.firstValue = c.valueArr[numOfHands];
        for (int j = 0; j < numOfHands; j++) {
            handsArr[j].secondValue = c.valueArr[numOfHands + 1 + j];
        }

        sumHandsValue();
    }

    private void sumHandsValue() {
        // hands 별 total value 구하기
        for (int i = 0; i < handsArr.length; i++) {
            handsArr[i].totalValue = handsArr[i].firstValue + handsArr[i].secondValue;

        }
    }

    private void printDividedCards() {

        for (int i = 0; i < handsArr.length; i++) {

            System.out.println();

            // ace 카드 유무 여부
            if (handsArr[i].firstValue == 1 || handsArr[i].secondValue == 1) {

                handsArr[i].pAce = true;
            } else {
                handsArr[i].pAce = false;
            }

            // 해당 핸드 블랙잭인 경우
            if ((handsArr[i].firstValue == 10 || handsArr[i].secondValue == 10) && handsArr[i].pAce) {

                handsArr[i].pBlackjack = true;

                System.out.printf("Hand %d: %s, %s\t total value: blackjack!!! congrat!!! \n", handsArr[i].handsNum,
                        handsArr[i].firstCard, handsArr[i].secondCard);
            }

            // 블랙잭 아니고, 해당 핸드 ace 있을 경우
            if ((handsArr[i].firstValue != 10 && handsArr[i].secondValue != 10) && handsArr[i].pAce) {

                System.out.printf("Hand %d: %s, %s\t total value: %d or %d \n", handsArr[i].handsNum,
                        handsArr[i].firstCard, handsArr[i].secondCard, handsArr[i].totalValue, handsArr[i].totalValue + 10);
            }

            // 블랙잭 아니고, ace도 없는 경우
            if (!handsArr[i].pBlackjack && !handsArr[i].pAce) {

                System.out.printf("Hand %d: %s, %s\t total value: %d \n", handsArr[i].handsNum,
                        handsArr[i].firstCard, handsArr[i].secondCard, handsArr[i].totalValue);
            }
        }
        System.out.printf("딜러 카드: %s\n", d.firstCard);
    }

    private void chooseHitOrStay() {

        for (int i = 0; i < handsArr.length; i++) {     // hand 수만큼 hit stay 묻기
            if (!handsArr[i].pBlackjack) askHisStay(i);
        }
    }

    private void askHisStay(int i) {

        int hitOrStay;

        System.out.println("\n======= " + handsArr[i].handsNum + "번 핸드 value: " + handsArr[i].totalValue +
                "\thit or stay (숫자를 입력하세요. 1번 : hit, 2번 : stay) =======");
        hitOrStay = s.nextInt();

        // hit 일 경우
        if (hitOrStay == 1) {
            oneMoreCard++;
            handsArr[i].totalValue += c.valueArr[dividedCardNum + oneMoreCard];

            // 핸드별로 받은 카드 중 ace 있고, 11 이하인 경우
            if ((c.valueArr[dividedCardNum + oneMoreCard] == 1 || handsArr[i].pAce) && handsArr[i].totalValue < 12) {
                handsArr[i].pAce = true;

                System.out.printf("%s 카드를 받았습니다. total value: %d or %d\n",
                        c.sixDeckCard[dividedCardNum + oneMoreCard], handsArr[i].totalValue, handsArr[i].totalValue + 10);
            } else {
                System.out.printf("%s 카드를 받았습니다. total value: %d\n",
                        c.sixDeckCard[dividedCardNum + oneMoreCard], handsArr[i].totalValue);
            }

            // Over 21 인 경우
            if (handsArr[i].totalValue > 21) {

                System.out.println("Over!!!");
                loose(i);
                over++;

                handsArr[i].pOver = true;

            } else {
                askHisStay(i);
            }
        }

        // stay 일 경우
        if (hitOrStay == 2) {
            System.out.println("stay");

            // ace 포함되어있고, 더한값이 11 이하인 경우
            if (handsArr[i].pAce && handsArr[i].totalValue < 12) {

                handsArr[i].totalValue += 10;
            }
        }
    }

    private void dealerGetCard() {

        oneMoreCard++;
        String nextCard = c.sixDeckCard[dividedCardNum + oneMoreCard];
        int nextCardValue = c.valueArr[dividedCardNum + oneMoreCard];

        d.dValueArr.add(nextCardValue);

        if (d.dValueArr.contains(1)) {
            d.dAce = true;
        } else d.dAce = false;

        System.out.printf("\n%s를 받았습니다.", nextCard);

        // 딜러 블랙잭
        if (((d.firstValue == 1 && nextCardValue == 10) || (d.firstValue == 10 && nextCardValue == 1))
                && d.dValueArr.size() == 2) {

            d.dBlackjack = true;
            end = true;
            System.out.println("딜러 blackjack...");
            return;
        }

        // ace 낀 경우
        if (d.dAce) {
            if (d.value + nextCardValue + 10 > 21) {

                System.out.printf("\n딜러 카드의 total value: %d", d.value + nextCardValue);
                d.value += nextCardValue;

                end = false;
            } else if (d.value + nextCardValue + 10 >= 17) {

                System.out.printf("\n딜러 카드의 total value: %d", d.value + nextCardValue + 10);
                d.value = d.value + nextCardValue + 10;

                end = true;
            } else {
                System.out.printf("\n딜러 카드의 total value: %d or %d\n",
                        d.value + nextCardValue, d.value + nextCardValue + 10);
                d.value += nextCardValue;

                end = false;
            }
        }

        // ace 없는 경우
        if (!d.dAce) {

            if (d.value + nextCardValue < 17) {

                System.out.printf("\n딜러 카드의 total value: %d\n", d.value + nextCardValue);
                d.value += nextCardValue;

                end = false;

            } else if (d.value + nextCardValue >= 17) {

                System.out.printf("\n딜러 카드의 total value: %d\n", d.value + nextCardValue);
                d.value += nextCardValue;

                end = true;
            }
        }
        // bust 인 경우
        if (d.value > 21) {

            d.dBust = true;
            end = true;
            System.out.println("딜러 bust!!! congratulations!!");
        }
    }

    private void win(int z) {

        System.out.println(handsArr[z].handsNum + "번 핸드 win");
        System.out.println("베팅금액 " + formatter.format(handsArr[z].bAmount) + "를 winning 하였습니다.");

        d.dChips -= handsArr[z].bAmount;
        p.pChips += handsArr[z].bAmount * 2;
        handsArr[z].bAmount = 0;
    }

    private void loose(int z) {

        System.out.println(handsArr[z].handsNum + "번 핸드 loose");
        System.out.println("베팅금액 " + formatter.format(handsArr[z].bAmount) + "를 loosing 하였습니다.");

        d.dChips += handsArr[z].bAmount;
        handsArr[z].bAmount = 0;
    }

    private void push(int z) {
        System.out.println(handsArr[z].handsNum + "번 핸드 push");

        p.pChips += handsArr[z].bAmount;
        handsArr[z].bAmount = 0;
    }

    // 손님 블랙잭으로 이길 시 1.5배 pay
    private void winBJ(int z) {

        System.out.println(handsArr[z].handsNum + "번 핸드 blackjack!!!");
        System.out.println("금액 " + formatter.format(handsArr[z].bAmount * 1.5) + "를 획득하였습니다.");

        d.dChips -= handsArr[z].bAmount * 1.5;
        p.pChips += handsArr[z].bAmount + (handsArr[z].bAmount * 1.5);
        handsArr[z].bAmount = 0;
    }

    private void payTake(int a) {

        if (handsArr[a].pOver) return;

        System.out.println();

        // 딜러 블랙잭, 손님 not 블랙잭
        if (d.dBlackjack && !handsArr[a].pBlackjack) {
            loose(a);
            return;
        }

        // 손님만 블랙잭
        if (!d.dBlackjack && handsArr[a].pBlackjack) {
            winBJ(a);
            return;
        }

        // 딜러 value 가 손님 value 와 같을 때, 딜러 손님 둘 다 블랙잭 일때
        if ((d.dBlackjack && handsArr[a].pBlackjack) || (d.value == handsArr[a].totalValue)) {
            push(a);
            return;
        }

        // 딜러 bust 일 때
        if (d.dBust) {
            win(a);
            return;
        }

        // 딜러 value 가 손님 value 보다 클때
        if (d.value > handsArr[a].totalValue) {
            loose(a);
            return;
        }

        // 딜러 value 가 손님 value 보다 작을 때
        if (d.value < handsArr[a].totalValue) {
            win(a);
            return;
        }

    }

    public void finalizeGame() {
        System.out.println();
        System.out.println("===== 게임종료 =====");
        System.out.println("칩스 잔액 : " + formatter.format(p.pChips) + "\t딜러 잔액 : "
                + formatter.format(d.dChips));

        System.out.println("게임을 계속 하려면 1, 종료하려면 2를 누르세요.");
        int a = s.nextInt();
        if (a == 1) {
            run();
        }
        if (a == 2) {
            return;
        }
    }

    public static void main(String[] args) {
        Table t = new Table();
        Thread thread = new Thread(t);

        thread.start();
    }

    @Override
    public void run() {
        startGame();

        System.out.println("===== No more bet!!! Game start =====");
        System.out.println("딜러 카드 뽑는 중 .....");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        divideCards(numOfHands);
        chooseHitOrStay();

        // 전부 다 over 되지 않은 경우에만 dealerGetCard() 실행
        if (this.over != numOfHands) {
            System.out.printf("\n======= 딜러 =======\n현재 딜러 카드: %s", d.firstCard);

            end = false;
            d.value = d.firstValue;

            d.dValueArr = new ArrayList<>();
            d.dValueArr.add(d.firstValue);

            while (!end ) {

                if (d.value > 16) break;

                dealerGetCard();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println();

        for (int a = 0; a < handsArr.length; a++) {

            payTake(a);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        finalizeGame();

    }


}
