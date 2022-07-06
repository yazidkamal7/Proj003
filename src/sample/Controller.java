package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class Controller implements Initializable {
    int numCity;
    int numDij;
    ArrayList<Vertex> cities=new ArrayList<>();
    List<String> city = new ArrayList<>();

    @FXML
    private ChoiceBox<String> sourceSelect;

    @FXML
    private ChoiceBox<String> TargetSelect;

    @FXML
    private TextArea pathTA;

    @FXML
    private TextField distancT;
    @FXML
    private AnchorPane bord;
    @FXML
    private ImageView MapImg;

    @FXML
    void Run(ActionEvent event) {

            distancT.clear();
            pathTA.clear();
            int sel=getIndex(sourceSelect.getValue());
            int target=getIndex(TargetSelect.getValue());


        computeShortestPaths(cities.get(sel),cities.get(target));
        List<Vertex> p = getShortestPathTo(cities.get(target));
        if(p.size() > 1 && cities.get(target).getDistance() != Double.MAX_VALUE){
            distancT.setText(String.valueOf(cities.get(target).getDistance()));
            for(int i =0 ; i<p.size();i++){
                pathTA.appendText((p.get(i).getName())+"->"+p.get(i).getDistance()+"\n");
            }

        }
        else {
            pathTA.setText("NO PATH FOUNDED");
        }
        for (int i = 0 ; i < p.size()-1 ; i++){
            drowline(p.get(i).getX(),p.get(i).getY(),p.get(i+1).getX(),p.get(i+1).getY());
        }

    }

    void drowline(double statX , double stary ,double endx,double endy){
        Line line=new Line(statX,stary,endx,endy);
        line.setStroke(Color.RED);
        bord.getChildren().add(line);
    }
    void readFile() {

        Scanner sc = null;
        try {
            sc = new Scanner(new BufferedReader(new FileReader("src\\sample\\city.txt")));
            numCity=sc.nextInt();
            numDij=sc.nextInt();
            sc.nextLine();
            for(int i = 0 ; sc.hasNextLine() && i<numCity;i++){
                String[] line;

                line = sc.nextLine().trim().split(String.valueOf(' '));
                Vertex c = new Vertex();
                c.setName(line[0]);
                city.add(line[0]);
                c.setX(Double.parseDouble(line[1]));
                c.setY(Double.parseDouble(line[2]));
                cities.add(c);
            }

            while (sc.hasNextLine()) {
                String[] line;
                line = sc.nextLine().trim().split(String.valueOf(' '));
                int c1=getIndex(line[0]);
                int c2=getIndex(line[1]);

                cities.get(c1).addNeighbour(new Edge(Double.parseDouble(line[2]),cities.get(c1),cities.get(c2)));
                cities.get(c2).addNeighbour(new Edge(Double.parseDouble(line[2]),cities.get(c2),cities.get(c1)));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("error!!!");
        }
    }
    int getIndex(String name){
        for (int i = 0 ; i < cities.size(); i++){
            if (cities.get(i).getName().equals(name))
                return i;
        }
        return cities.size()+1;

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        readFile();
        sourceSelect.setItems(FXCollections.observableArrayList(city));
        TargetSelect.setItems(FXCollections.observableArrayList(city));

        for(int i = 0 ; i<cities.size();i++){
            Text text=new Text(cities.get(i).getX()+5,cities.get(i).getY()+5,cities.get(i).getName());
            text.setFont(Font.font(14));
            Circle circle = new Circle(cities.get(i).getX(), cities.get(i).getY(), 4);

            bord.getChildren().add(circle);
            bord.getChildren().add(text);

        }
    }

    public void computeShortestPaths( Vertex sourceVertex,Vertex target ){

        sourceVertex.setDistance(0);
        PriorityQueue<Vertex> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(sourceVertex);

        sourceVertex.setVisited(true);

        while( !priorityQueue.isEmpty() ){
            Vertex actualVertex = priorityQueue.poll();
            if(actualVertex.equals(target)){
                break;

            }
            for(Edge edge : actualVertex.getAdjacenciesList()){

                Vertex v = edge.getTargetVertex();
                if(!v.isVisited())
                {
                    double newDistance = actualVertex.getDistance() + edge.getWeight();

                    if( newDistance < v.getDistance() ){
                        priorityQueue.remove(v);
                        v.setDistance(newDistance);
                        v.setPredecessor(actualVertex);
                        priorityQueue.add(v);
                    }
                }
            }
            actualVertex.setVisited(true);
        }


    }

    public List<Vertex> getShortestPathTo(Vertex targetVertex){

        List<Vertex> path = new ArrayList<>();


        for(Vertex vertex=targetVertex;vertex!=null;vertex=vertex.getPredecessor()){

            path.add(vertex);
        }

        Collections.reverse(path);

        return path;
    }
}
