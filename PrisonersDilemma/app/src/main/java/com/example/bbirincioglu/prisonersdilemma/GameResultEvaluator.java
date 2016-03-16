package com.example.bbirincioglu.prisonersdilemma;

/**
 * Created by bbirincioglu on 3/16/2016.
 */
public class GameResultEvaluator {
    public static final String CHOICE_COOPERATE = "COOPERATE";
    public static final String CHOICE_DEFECT = "DEFECT";

    public GameResultEvaluator() {

    }

    public String evaluate(GameResult gameResult) {
        String result = null;

        String p1Name = gameResult.getP1Name();
        String p1Surname = gameResult.getP1Surname();
        String p1Commitment = gameResult.getP1Commitment();
        String p1Decision = gameResult.getP1Decision();

        String p2Name = gameResult.getP2Name();
        String p2Surname = gameResult.getP2Surname();
        String p2Commitment = gameResult.getP2Commitment();
        String p2Decision = gameResult.getP2Decision();

        boolean withCommitment = Boolean.valueOf(gameResult.getWithCommitment());
        char splitWith = ',';
        int[] copCop = stringToIntArray(gameResult.getCopCop(), splitWith);
        int[] copDef = stringToIntArray(gameResult.getCopDef(), splitWith);
        int[] defCop = stringToIntArray(gameResult.getDefCop(), splitWith);
        int[] defDef = stringToIntArray(gameResult.getDefDef(), splitWith);
        int punishment = Integer.valueOf(gameResult.getPunishment());

        if (withCommitment) {
            if (p1Decision.equals(CHOICE_COOPERATE) && p2Decision.equals(CHOICE_COOPERATE)) {

            } else if (p1Decision.equals(CHOICE_COOPERATE) && p2Decision.equals(CHOICE_DEFECT)) {

            } else if (p1Decision.equals(CHOICE_DEFECT) && p2Decision.equals(CHOICE_COOPERATE)) {

            } else if (p1Decision.equals(CHOICE_DEFECT) && p2Decision.equals(CHOICE_DEFECT)) {

            }
        } else {
            if (p1Decision.equals(CHOICE_COOPERATE) && p2Decision.equals(CHOICE_COOPERATE)) {

            } else if (p1Decision.equals(CHOICE_COOPERATE) && p2Decision.equals(CHOICE_DEFECT)) {

            } else if (p1Decision.equals(CHOICE_DEFECT) && p2Decision.equals(CHOICE_COOPERATE)) {

            } else if (p1Decision.equals(CHOICE_DEFECT) && p2Decision.equals(CHOICE_DEFECT)) {

            }
        }

        return result;
    }

    private int[] stringToIntArray(String text, char splitWith) {
        int[] result = new int[2];
        String temp = "";
        int length = text.length();
        int index = 0;

        for (int i = 0; i < length; i++) {
            char charAtI = text.charAt(i);

            if (charAtI == splitWith) {
                result[index] = Integer.valueOf(temp);
                temp = "";
                index++;
            } else {
                temp += charAtI;
            }
        }

        if (!temp.equals("")) {
            result[index] = Integer.valueOf(temp);
        }

        return result;
    }
}
