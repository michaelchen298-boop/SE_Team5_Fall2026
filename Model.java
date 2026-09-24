// update all sprites
public class Model {

    private PlayerDatabase playerDatabase;

    public Model() {
        playerDatabase = new PlayerDatabase();
    }

    public void update() {
        // Implement the logic to update the model state
    }

    public boolean updatePlayer(int id, String newCodename) {
        return playerDatabase.updatePlayer(id, newCodename);
    }

    public PlayerDatabase getPlayerDatabase() {
        return playerDatabase;
    }
}
