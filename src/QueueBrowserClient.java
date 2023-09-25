import org.apache.axis2.AxisFault;
import org.wso2.andes.client.AndesClient;
import org.wso2.andes.client.BasicMessageConsumer;
import org.wso2.andes.client.BasicMessageConsumer_0_10;
import org.wso2.andes.client.HierarchicalConfigurationChangeListener;
import org.wso2.andes.client.JNDIConfigurationChangeListener;
import org.wso2.andes.client.LocalAMQConnectionFactory;
import org.wso2.andes.configuration.AndesConfigurationManager;
import org.wso2.andes.configuration.ClientProperties;
import org.wso2.andes.kernel.AndesContext;
import org.wso2.andes.server.ClusterResourceHolder;
import org.wso2.andes.server.registry.ApplicationRegistry;
import org.wso2.andes.wso2.QueueBrowser;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Properties;

public class QueueBrowserClient {
    private static final String QUEUE_NAME = "YourQueueName";
    private static final String CONNECTION_FACTORY_NAME = "ConnectionFactory";
    private static final String PROVIDER_URL = "amqp://admin:admin@localhost:5672";

    public static void main(String[] args) {
        Connection connection = null;
        Session session = null;
        try {
            // Set up JNDI properties
            Properties properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wso2.andes.jndi.PropertiesFileInitialContextFactory");
            properties.put(Context.PROVIDER_URL, PROVIDER_URL);

            // Create initial context
            Context context = new InitialContext(properties);

            // Lookup ConnectionFactory
            LocalAMQConnectionFactory connectionFactory = (LocalAMQConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);

            // Create connection
            connection = connectionFactory.createConnection();
            connection.start();

            // Create session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Lookup queue
            Queue queue = session.createQueue(QUEUE_NAME);

            // Create queue browser
            QueueBrowser browser = session.createBrowser(queue);

            // Browse messages
            Enumeration<?> messages = browser.getEnumeration();
            while (messages.hasMoreElements()) {
                Message message = (Message) messages.nextElement();
                System.out.println("Message ID: " + message.getJMSMessageID());
                // Process the message as needed
            }

            // Close browser and session
            browser.close();
            session.close();

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}