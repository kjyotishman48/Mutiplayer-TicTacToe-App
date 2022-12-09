package androidsamples.java.tictactoe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;


public class GameFragment extends Fragment {
  private static final String TAG = "GameFragment";
  private static final int GRID_SIZE = 9;
  String dialogMessage = "";
  private final Button[] mButtons = new Button[GRID_SIZE];
  private NavController mNavController;


  private boolean isSinglePlayer = true;
  private String myChar = "X";
  private String otherChar = "O";
  private boolean myTurn = true;
  private String[] gameArray = new String[]{"", "", "", "", "", "", "", "", ""};
  private boolean gameEnded = false;
  private GameModel game;
  private boolean isHost = true;
  private DatabaseReference gameReference, userReference;

  private String hostLeft = "false";
  private String guestLeft = "false";
  private boolean userQuit = false;
  private String isOpen = "false";

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setHasOptionsMenu(true); // Needed to display the action menu for this fragment

    // Extract the argument passed with the action in a type-safe way
    GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
    Log.d(TAG, "New game type = " + args.getGameType());
    isSinglePlayer = (args.getGameType().equals("One-Player"));

    userReference = FirebaseDatabase.getInstance("https://tictactoe-c4bfa-default-rtdb.firebaseio.com/").getReference("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());


    gameReference = FirebaseDatabase.getInstance("https://tictactoe-c4bfa-default-rtdb.firebaseio.com/").getReference("games").child(args.getGameId());
    gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        Log.d(TAG,"Line 76");
        game = snapshot.getValue(GameModel.class);
        assert game != null;
        gameArray = (game.getGameArray()).toArray(new String[9]);
        updateUI();
        updateContentDescription();
        if (game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
          isHost = true;
          myTurn = updateTurn(game.getTurn());
          myChar = "X";
          otherChar = "O";
          otherChar = "O";
        } else {
          isHost = false;
          myTurn = updateTurn(game.getTurn());
          myChar = "O";
          otherChar = "X";
        }
      }
      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e("Game setup error", error.getMessage());
      }
    });

    // Handle the back press by adding a confirmation dialog
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        userQuit = true;
        Log.d(TAG, "Back pressed"+gameEnded);
        if (!gameEnded) {
          Log.d(TAG,"Game Ended");
          AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                  .setTitle(R.string.confirm)
                  .setMessage(R.string.forfeit_game_dialog_message)
                  .setPositiveButton(R.string.yes, (d, which) -> {
                    gameEnded = true;
                    if (!isSinglePlayer) {
                      gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                          Log.d(TAG,"Line 118");
                          Log.d(TAG,"Closing game");
                          game.setIsOpen(false);
                          snapshot.getRef().child("isOpen").setValue(false);
                          if(game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            game.setHostLeft(true);
                            snapshot.getRef().child("hostLeft").setValue(true);
                            isOpen = Objects.requireNonNull(snapshot.child("isOpen").getValue()).toString();
                            Log.d(TAG,"is Open = "+isOpen);
                          }
                          else {
                            game.setGuestLeft(true);
                            snapshot.getRef().child("guestLeft").setValue(true);
                          }
                          checkIfUserLeft();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                      });
                    }
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG,"Line 144");
                        if(isOpen.equals("false")) {
                          int value = Integer.parseInt(dataSnapshot.child("lost").getValue().toString());
                          value = value + 1;
                          dataSnapshot.getRef().child("lost").setValue(value);
                        }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {

                      }
                    });
                    mNavController.popBackStack();
                  })
                  .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                  .create();
          dialog.show();
        } else {
          NavHostFragment.findNavController(getParentFragment()).navigateUp();
        }
      }
    };
    requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
  }

  public void checkIfUserLeft() {
    Log.d(TAG,"Checking if user left"+" Host Left = "+hostLeft+" Guest Left = "+guestLeft);
    if(hostLeft.equals("true") && !((game.getHost()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))) {
      Log.d(TAG,"Host left, this is the Guest");
      AlertDialog dialog_left = new AlertDialog.Builder(requireActivity())
              .setTitle("Game Result").setMessage("The host has left the game you win")
              .setPositiveButton(R.string.ok, (d, which)->{
                userQuit = true;
                endGame(1);
                mNavController.popBackStack();
              }).create();dialog_left.show();
    }
    else if(guestLeft.equals("true") && (game.getHost()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
      Log.d(TAG,"Guest left, this is the host");
      AlertDialog dialog_left = new AlertDialog.Builder(requireActivity())
              .setTitle("Game Result").setMessage("The guest has left the game you win")
              .setPositiveButton(R.string.ok, (d, which)->{
                userQuit = true;
                endGame(1);
                mNavController.popBackStack();
              }).create();dialog_left.show();
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_game, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mNavController = Navigation.findNavController(view);

    mButtons[0] = view.findViewById(R.id.button0);
    mButtons[1] = view.findViewById(R.id.button1);
    mButtons[2] = view.findViewById(R.id.button2);

    mButtons[3] = view.findViewById(R.id.button3);
    mButtons[4] = view.findViewById(R.id.button4);
    mButtons[5] = view.findViewById(R.id.button5);

    mButtons[6] = view.findViewById(R.id.button6);
    mButtons[7] = view.findViewById(R.id.button7);
    mButtons[8] = view.findViewById(R.id.button8);

    for (int i = 0; i < mButtons.length; i++) {
      int finalI = i;
      mButtons[i].setOnClickListener(v -> {
        if (myTurn){
          Log.d(TAG, "Button " + finalI + " clicked");
          ((Button) v).setText(myChar);
          gameArray[finalI] = myChar;
          updateDB();
          v.setClickable(false);
          updateContentDescription();
          int win = checkWin();
          if (win!=0) {
            endGame(win);
            return;
          }
          myTurn = !myTurn;
          if (isSinglePlayer) {
            automateSinglePlayerGame();
          }
        } else {
          Toast.makeText(getContext(), "Please wait for your turn!", Toast.LENGTH_SHORT).show();
        }
      });
    }

    if(!isSinglePlayer) {
      gameReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          Log.d(TAG,"Line 248" + gameEnded);
          hostLeft = snapshot.child("hostLeft").getValue().toString();
          guestLeft = snapshot.child("guestLeft").getValue().toString();
          Log.d(TAG, "hostleft = "+hostLeft+ " guestleft = "+guestLeft);
          Log.d(TAG,"GAME ENDED ="+gameEnded);
          if(!userQuit) {
            checkIfUserLeft();
          }
          GameModel latestGameModel = snapshot.getValue(GameModel.class);
          assert latestGameModel != null;
          game.updateGameArray(latestGameModel);
          gameArray = (game.getGameArray()).toArray(new String[9]);
          isOpen = snapshot.child("isOpen").getValue().toString();
          Log.d(TAG,"is Open = "+isOpen);
          updateUI();
          updateContentDescription();
          myTurn = updateTurn(game.getTurn());
          int win = checkWin();
          if (win !=0) endGame(win);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });
    }
  }

  private void automateSinglePlayerGame() {
    Random rand = new Random();
    int x = rand.nextInt(9);
    while (!gameArray[x].isEmpty()) x = rand.nextInt(9);
    gameArray[x] = otherChar;
    mButtons[x].setText(otherChar);
    mButtons[x].setClickable(false);
    updateDB();
    updateContentDescription();
    myTurn = !myTurn;
    int win = checkWin();
    if (win != 0) endGame(win);
  }

  private boolean checkDraw() {
    for (int i = 0; i < 9; i++) {
      if (gameArray[i].isEmpty()) {
        return false;
      }
    }
    return true;
  }

  private int checkWin() {
    String winChar = "";
    if  (gameArray[0].equals(gameArray[1]) && gameArray[1].equals(gameArray[2]) && !gameArray[0].isEmpty()) winChar = gameArray[0];
    else if (gameArray[3].equals(gameArray[4]) && gameArray[4].equals(gameArray[5]) && !gameArray[3].isEmpty()) winChar = gameArray[3];
    else if (gameArray[6].equals(gameArray[7]) && gameArray[7].equals(gameArray[8]) && !gameArray[6].isEmpty()) winChar = gameArray[6];
    else if (gameArray[0].equals(gameArray[3]) && gameArray[3].equals(gameArray[6]) && !gameArray[0].isEmpty()) winChar = gameArray[0];
    else if (gameArray[4].equals(gameArray[1]) && gameArray[1].equals(gameArray[7]) && !gameArray[1].isEmpty()) winChar = gameArray[1];
    else if (gameArray[2].equals(gameArray[5]) && gameArray[5].equals(gameArray[8]) && !gameArray[2].isEmpty()) winChar = gameArray[2];
    else if (gameArray[0].equals(gameArray[4]) && gameArray[4].equals(gameArray[8]) && !gameArray[0].isEmpty()) winChar = gameArray[0];
    else if (gameArray[6].equals(gameArray[4]) && gameArray[4].equals(gameArray[2]) && !gameArray[2].isEmpty()) winChar = gameArray[2];
    else if(checkDraw()) return 2; // for draw
    else return 0;

    return (winChar.equals(myChar)) ? 1 : -1;
  }

  private void updateDB() {
    gameReference.child("gameArray").setValue(Arrays.asList(gameArray));
    if (game.getTurn() == 1) {
      game.setTurn(2);
      gameReference.child("turn").setValue(game.getTurn());
    } else if(game.getTurn()==2){
      game.setTurn(1);
      gameReference.child("turn").setValue(game.getTurn());
    }
  }

  private void endGame(int win) {
    switch (win) {
      case 1:
        dialogMessage = "Congratulations, you win !";
        if(!gameEnded) {
          userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              Log.d(TAG,"Line 334");
              int value = Integer.parseInt(dataSnapshot.child("won").getValue().toString());
              value = value + 1;
              dataSnapshot.getRef().child("won").setValue(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
          });
        }
        break;
      case -1:
        dialogMessage = "Sorry, you lose.";
        if(!gameEnded) {
          userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              Log.d(TAG,"Line 353");
              int value = Integer.parseInt(dataSnapshot.child("lost").getValue().toString());
              value = value + 1;
              dataSnapshot.getRef().child("lost").setValue(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
          });
        }
        break;
      case 2:
        dialogMessage = "Game is drawn";
        if(!gameEnded) {
          userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              Log.d(TAG,"Line 372");
              int value = Integer.parseInt(snapshot.child("draw").getValue().toString());
              value = value + 1;
              snapshot.getRef().child("draw").setValue(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
          });
        }
    }
    if(!userQuit && !gameEnded) {
      //since have to show to show this dialog box only if the game ends with neither of host or guest quitting
      AlertDialog dialog = new AlertDialog.Builder(requireActivity())
              .setTitle("Game Result").setMessage(dialogMessage)
              .setPositiveButton(R.string.ok, (d, which) -> {
                mNavController.popBackStack();
              }).create();
      dialog.show();
    }
    gameEnded = true;
  }

  private boolean updateTurn (int turn) {
    return (turn == 1) == isHost;
  }

  private void updateUI() {
    for (int i = 0; i < 9; i++) {
      String v = gameArray[i];
      if (!v.isEmpty()) {
        mButtons[i].setText(v);
        mButtons[i].setClickable(false);
      }
    }
  }

//  @Override
//  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//    super.onCreateOptionsMenu(menu, inflater);
//    inflater.inflate(R.menu.menu_logout, menu);
//    // this action menu is handled in MainActivity
//  }

  private void updateContentDescription() {
    for(int i=0; i<9; i++) {
      String ch = gameArray[i];
      if(ch=="X") {
        mButtons[i].setContentDescription("The content of grid "+ (i+1) +" is cross");
      }
      else if(ch=="O") {
        mButtons[i].setContentDescription("The content of grid "+ (i+1) +" is nought");
      }
      else {
        mButtons[i].setContentDescription("Grid "+ (i+1) + "is not yet set, double press to set this grid");
      }
    }
  }
}