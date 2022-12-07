package androidsamples.java.tictactoe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class OpenGamesAdapter extends RecyclerView.Adapter<OpenGamesAdapter.ViewHolder> {

  private ArrayList<GameModel> list;
  private NavController navController;
  private String gameId;
  private DatabaseReference gameReference;

  public OpenGamesAdapter(ArrayList<GameModel> list, NavController navController) {
    // FIXME if needed
    this.list = list;
    this.navController = navController;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    // TODO bind the item at the given position to the holder
    //change getHostEmail to ID
    holder.populate(list.get(position).getHostEmail(),position+1, navController);
    gameId = list.get(position).getGameId();
  }

  @Override
  public int getItemCount() {
    return list.size(); // FIXME
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mIdView = view.findViewById(R.id.item_number);
      mContentView = view.findViewById(R.id.content);
    }

    @NonNull
    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }

    public void populate (String game, int i, NavController nav) {
      mContentView.setText(game+" is hosting, tap to join");
      mIdView.setText(i+")");
      mView.setOnClickListener(v -> {
        gameReference = FirebaseDatabase.getInstance("https://tictactoe-c4bfa-default-rtdb.firebaseio.com/").getReference("games").child(gameId);
        gameReference.child("isOpen").setValue(false);
        gameReference.child("turn").setValue(1);
        NavDirections action = DashboardFragmentDirections.actionGame("Two-Player", gameId);
        nav.navigate(action);
        //Navigation.findNavController(mView).navigate(action);
      });
    }
  }
}