package com.company;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;

class Security {
    static public String generateKey() {
        StringBuilder sb = null;
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            byte[] values = new byte[16];
            random.nextBytes(values);
            sb = new StringBuilder();
            for (byte b : values) { sb.append(String.format("%02x", b)); }
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return sb.toString();
    }

    static public void calcHmacSha256(byte[] secretKey, byte[] message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacSha256 = mac.doFinal(message);
            System.out.println(String.format("HMAC: %02x", new BigInteger(1, hmacSha256)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
    }
}

class Game {
    protected static int makeMove(String[] moves, int moveslength){
        int status = 0;
        int myInput = 0;
        do {
            System.out.println("Available moves:");
            for (int i = 0; i < moveslength; i++) {
                System.out.println(i+1 + " - " + moves[i]);
            }
            System.out.print("0 - exit\nEnter your move: ");
            Scanner myMoveScan = new Scanner(System.in);
            if (myMoveScan.hasNextInt()) {
                myInput = (myMoveScan.nextInt());
                if (myInput == 0) {
                    System.exit(0);
                } else if (myInput <= moveslength && myInput >0){
                    System.out.println("Your move: " + moves[myInput - 1]);
                    status = 1;
                } else { System.out.println("\nWrong move, try again.\n"); }
            } else { System.out.println("\nWrong move, try again.\n"); }
        } while (status != 1);
        return (myInput - 1);
    }

    protected static void play(int humanMove, int computerMove, int length) {
        int human = humanMove - 1;
        int computer = computerMove - 1;
        if (human == computer) {
            System.out.println("Friendship wins");
        } else {
            int medium = length / 2;
            String result = null;
            if (human > computer) result = (human - computer) > medium ? "You lose" : "You win";
            if (human < computer) result = (computer - human) > medium ? "You win" : "You lose";
            System.out.println(result);
        }
    }
}

public class Main {
        public static void main(String[] moves) throws UnsupportedEncodingException {
        int movesLength = moves.length;
            if (movesLength % 2 == 0 || movesLength < 3 || Arrays.stream(moves).distinct().count() < moves.length) {
            System.out.println("Incorrect parameters. Use only odd number of unique values, at least three.");
            System.exit(0);
        }
        String secretKey = Security.generateKey();
        int compMove = (int) (Math.random() * movesLength);
        Security.calcHmacSha256(secretKey.getBytes("UTF-8"), moves[compMove].getBytes("UTF-8"));
        int myMove = Game.makeMove(moves, movesLength);
        System.out.println("Computer move: " + moves[compMove] + " - " + (compMove+1));
        Game.play(myMove, compMove, movesLength);
        System.out.println("HMAC key: " + secretKey);
    }
}