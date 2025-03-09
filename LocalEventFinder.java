import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class LocalEventFinder {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JTextField cityField;
    private JButton searchButton;

    public LocalEventFinder() {
        frame = new JFrame("Local Event & Festival Finder");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(30, 30, 60)); // Dark Blue

        JPanel panel = new JPanel();
        panel.setBackground(new Color(50, 50, 100));
        cityField = new JTextField(15);
        searchButton = new JButton("Find Events");
        searchButton.addActionListener(this::fetchEvents);

        panel.add(new JLabel("Enter City:"));
        panel.add(cityField);
        panel.add(searchButton);
        frame.add(panel, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(new String[]{"Event Name", "Category", "Date", "URL"}, 0);
        table = new JTable(model);
        table.setBackground(new Color(200, 220, 240));
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void fetchEvents(ActionEvent e) {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a city!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String apiKey = "N657HBO4KW3YPSGM2Q5V";
        String urlString = "https://www.eventbriteapi.com/v3/events/search/?location.address=" + city + "&token=" + apiKey;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder jsonResponse = new StringBuilder();
            while (scanner.hasNext()) {
                jsonResponse.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject jsonObject = new JSONObject(jsonResponse.toString());
            JSONArray events = jsonObject.getJSONArray("events");

            model.setRowCount(0);
            for (int i = 0; i < Math.min(10, events.length()); i++) {
                JSONObject event = events.getJSONObject(i);
                String name = event.getJSONObject("name").getString("text");
                String category = event.has("category_id") ? event.getString("category_id") : "General";
                String date = event.getJSONObject("start").getString("local");
                String urlEvent = event.getString("url");
                model.addRow(new Object[]{name, category, date, urlEvent});
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error fetching events!", "API Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LocalEventFinder::new);
    }
}
