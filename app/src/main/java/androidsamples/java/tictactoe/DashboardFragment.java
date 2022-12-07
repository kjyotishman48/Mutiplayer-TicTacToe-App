package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

  private static final String TAG = "DashboardFragment";
  private NavController mNavController;
  private FirebaseAuth auth;
  private DatabaseReference gamesRef, usersRef;
  private RecyclerView rv;
  private TextView won, lost, draw, openGamesLabel, loginInfo;
  private String email_id;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public DashboardFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setHasOptionsMenu(true); // Needed to display the action menu for this fragment
    gamesRef = FirebaseDatabase.getInstance("https://tictactoe-c4bfa-default-rtdb.firebaseio.com/").getReference("games");
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_dashboard, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mNavController = Navigation.findNavController(view);

    rv = view.findViewById(R.id.list);
    won = view.findViewById(R.id.won_score);
    lost = view.findViewById(R.id.lost_score);
    draw = view.findViewById(R.id.draw_score);
    openGamesLabel = view.findViewById(R.id.open_games_label);
    loginInfo = view.findViewById(R.id.login_info);
    //if a user is not logged in, go to LoginFragment

    auth = FirebaseAuth.getInstance();
    if(auth.getCurrentUser() == null) {
      mNavController.navigate(R.id.action_need_auth);
      return;
    }

    usersRef = FirebaseDatabase.getInstance("https://tictactoe-c4bfa-default-rtdb.firebaseio.com/").getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    ArrayList<GameModel> gameList = new ArrayList<>();
    gamesRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        gameList.clear();
        for (DataSnapshot shot : snapshot.getChildren()) {
          GameModel game = shot.getValue(GameModel.class);
          if (game.getIsOpen() && !game.getHost().equals(auth.getCurrentUser().getUid())) gameList.add(game);
        }
        rv.setAdapter(new OpenGamesAdapter(gameList, mNavController));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        openGamesLabel.setText(gameList.isEmpty() ? "No Open Games Available" : gameList.size()+" multiplayer games are open");
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });

    usersRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        loginInfo.setText("Logged in as "+snapshot.child("email").getValue().toString());
        loginInfo.setContentDescription("You are logged in as "+snapshot.child("email").getValue().toString());
        won.setText(snapshot.child("won").getValue().toString());
        won.setContentDescription("The number of games you have won is "+snapshot.child("won").getValue().toString());
        lost.setText(snapshot.child("lost").getValue().toString());
        lost.setContentDescription("The number of games you have won is "+snapshot.child("lost").getValue().toString());
        draw.setText(snapshot.child("draw").getValue().toString());
        draw.setContentDescription("The number of games you have drawn is "+snapshot.child("draw").getValue().toString());
        email_id = snapshot.child("email").getValue().toString();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });

    // Show a dialog when the user clicks the "new game" button
    view.findViewById(R.id.fab_new_game).setOnClickListener(v -> {

      // A listener for the positive and negative buttons of the dialog
      DialogInterface.OnClickListener listener = (dialog, which) -> {
        String gameType = "No type";
        String gameId = "Single Game ID";
        if (which == DialogInterface.BUTTON_POSITIVE) {
          gameType = getString(R.string.two_player);
          gameId = gamesRef.push().getKey();
          assert gameId != null;
          gamesRef.child(gameId).setValue(new GameModel(FirebaseAuth.getInstance().getCurrentUser().getUid(), gameId, email_id, false, false));
          Log.i("FIREBASE", "Value set");
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
          gameType = getString(R.string.one_player);
        }
        Log.d(TAG, "New Game: " + gameType);

        // Passing the game type as a parameter to the action
        // extract it in GameFragment in a type safe way
        NavDirections action = (NavDirections) DashboardFragmentDirections.actionGame(gameType, gameId);
        mNavController.navigate(action);
      };

      // create the dialog
      AlertDialog dialog = new AlertDialog.Builder(requireActivity())
              .setTitle(R.string.new_game)
              .setMessage(R.string.new_game_dialog_message)
              .setPositiveButton(R.string.two_player, listener)
              .setNegativeButton(R.string.one_player, listener)
              .setNeutralButton(R.string.cancel, (d, which) -> d.dismiss())
              .create();
      dialog.show();
    });
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
  }
}