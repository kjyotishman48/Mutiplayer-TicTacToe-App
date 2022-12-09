package androidsamples.java.tictactoe;

import java.util.Arrays;
import java.util.List;

public class GameModel {

    private List<String> gameArray = null;
    private String host;
    private Boolean isOpen;
    private String gameId;
    private int turn;
    private String hostEmail;

    private boolean hostLeft;
    private boolean guestLeft;

    public GameModel(String host, String id, String email, boolean hostLeft, boolean guestLeft, boolean isOpen, int turn) {
        this.host = host;
        this.isOpen = isOpen;
        gameArray = Arrays.asList("", "", "", "", "", "", "", "", "");
        this.gameId = id;
        hostEmail = email;
        this.hostLeft = hostLeft;
        this.guestLeft = guestLeft;
        this.turn = turn;
    }

    public GameModel(){}

    public List<String> getGameArray() {
        return gameArray;
    }

    public void setGameArray(List<String> gameArray) {
        this.gameArray = (gameArray);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean open) {
        isOpen = open;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void updateGameArray(GameModel o) {
        gameArray = o.gameArray;
        turn = o.turn;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setHostEmail(String email) {
        hostEmail = email;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public boolean getHostLeft() {
        return hostLeft;
    }

    public boolean getGuestLeft() {
        return guestLeft;
    }

    public void setHostLeft(boolean setValue) {
        hostLeft = setValue;
    }

    public void setGuestLeft(boolean setValue) {
        guestLeft = setValue;
    }

}