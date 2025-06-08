package persistencia;

public class JSONDAOFactory implements DAOFactory {

	@Override
    public ConversacionDAO createConversacionDAO() {
        return new JSONConversacionDAO();
    }

}
