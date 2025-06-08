package persistencia;

public class TextDAOFactory implements DAOFactory {

	@Override
    public ConversacionDAO createConversacionDAO() {
        return new TextConversacionDAO();
    }

}
