package persistencia;

public class XMLDAOFactory implements DAOFactory {

	@Override
    public ConversacionDAO createConversacionDAO() {
        return new XMLConversacionDAO();
    }

}
