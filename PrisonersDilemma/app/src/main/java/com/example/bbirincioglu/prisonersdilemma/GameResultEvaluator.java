package com.example.bbirincioglu.prisonersdilemma;

/**
 * The class for evaluating final decisions of the players, and determining which player obtained which payoff.
 */
public class GameResultEvaluator {
    public static final String CHOICE_COOPERATE = "COOPERATE";
    public static final String CHOICE_DEFECT = "DEFECT";

    public GameResultEvaluator() {

    }

    //Determine which player gets how much payoff by considering their decisions, commitments etc.
    public int[] evaluate(GameResult gameResult) {
        int[] result = null;

        String p1Commitment = gameResult.getP1Commitment(); //get commitment of player 1.
        String p1Decision = gameResult.getP1Decision(); //get decision of player 1.

        String p2Commitment = gameResult.getP2Commitment(); //get commitment of player 2.
        String p2Decision = gameResult.getP2Decision(); //get decision of player 2.

        boolean withCommitment = Boolean.valueOf(gameResult.getWithCommitment()); //get whether game is played with commitment or not.
        char splitWith = ',';
        int[] copCop = stringToIntArray(gameResult.getCopCop(), splitWith);
        int[] copDef = stringToIntArray(gameResult.getCopDef(), splitWith);
        int[] defCop = stringToIntArray(gameResult.getDefCop(), splitWith);
        int[] defDef = stringToIntArray(gameResult.getDefDef(), splitWith);
        int punishment = Integer.valueOf(gameResult.getPunishment());

        if (p1Decision.equals(CHOICE_COOPERATE) && p2Decision.equals(CHOICE_COOPERATE)) {
            result = copCop;
        } else if (p1Decision.equals(CHOICE_COOPERATE) && p2Decision.equals(CHOICE_DEFECT)) {
            result = copDef;
        } else if (p1Decision.equals(CHOICE_DEFECT) && p2Decision.equals(CHOICE_COOPERATE)) {
            result = defCop;
        } else if (p1Decision.equals(CHOICE_DEFECT) && p2Decision.equals(CHOICE_DEFECT)) {
            result = defDef;
        }

        if (withCommitment) {
            if (!p1Commitment.equals(p1Decision)) { //Commitment is not the same as decision, thus apply punishment to player 1.
                result[0] = result[0] + punishment;
            }

            if (!p2Commitment.equals(p2Decision)) { //Commitment is not the same as decision, thus apply punishment to player 2.
                result[1] = result[1] + punishment;
            }
        }

        return result;
    }

    //takes string, and divides string according to "splitWith" character, and returns substrings of "text" as string array.
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
