import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;


import java.util.Scanner;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;

public class Product {

    public static void main (String[] args) {

        String uri = "mongodb://localhost:27017";
        Scanner sc = new Scanner(System.in);

        try (MongoClient mongoClient = MongoClients.create(uri))
        {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("Products");
            MongoCollection<Document> collection = mongoDatabase.getCollection("ListOfProducts");

            Bson projectionFields = Projections.fields(
                    Projections.include("Name", "Model", "Price", "Country"),
                    Projections.excludeId());

            int choice;

            do {
                System.out.println("1)Show products");
                System.out.println("2)Sort ascending");
                System.out.println("3)Sort desceding");
                System.out.println("4)Insert new document");
                System.out.println("5)Update exist document");
                System.out.println("6)Delete doc");
                System.out.println("7)Quit");

                choice = sc.nextInt();

                if (choice == 7)
                {
                    break;
                }

                switch (choice)
                {
                    case 1:
                        ShowProducts(collection, projectionFields);
                        break;
                    case 2:
                        A_Show(collection, projectionFields);
                        break;
                    case 3:
                        D_Show(collection, projectionFields);
                        break;
                    case 4:
                        Insert_Product(collection, sc);
                        break;
                    case 5:
                        UpdatePrice(collection, projectionFields, sc);
                        break;
                    case 6:
                        DeleteDoc(collection, sc);
                        break;
                }

            }while (true);
        }


    }

    public static void ShowProducts (MongoCollection<Document> collection, Bson projectionFields)
    {
        MongoCursor<Document> cursor = collection.find().projection(projectionFields).iterator();

        try {
            while(cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
                System.out.println();
            }
        } finally {
            cursor.close();
        }
    }

    public static void A_Show (MongoCollection<Document> collection, Bson projectionFields)
    {
        MongoCursor<Document> cursor = collection.find().projection(projectionFields)
                .sort(Sorts.ascending("Price")).iterator();

        try {
            while(cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }
    public static void D_Show (MongoCollection<Document> collection, Bson projectionFields)
    {
        MongoCursor<Document> cursor = collection.find().projection(projectionFields)
                .sort(Sorts.descending("Price")).iterator();

        try {
            while(cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    public static void Insert_Product (MongoCollection<Document> collection, Scanner sc)
    {
        String name;
        String model;
        int price;
        String country;

        sc.nextLine();
        do {
            System.out.println("Enter name:");
            name = sc.nextLine();

            if(name.isEmpty())
            {
                System.out.println("Invalid, try again");
            }
        }while (name.isEmpty());

        do {
            System.out.println("Enter model:");
            model = sc.nextLine();

            if (model.isEmpty())
            {
                System.out.println("Invalid, try again");
            }
        }while (model.isEmpty());

        do {
            System.out.println("Enter price:");
            price = sc.nextInt();
            sc.nextLine();

            if (price <= 0)
            {
                System.out.println("Invalid, try again");
            }
        }while (price <= 0);

        do {
            System.out.println("Enter country:");
            country = sc.nextLine();

            if(country.isEmpty())
            {
                System.out.println("Invalid, try again");
            }
        }while (country.isEmpty());

        try {
            InsertOneResult result = collection.insertOne(new Document()
                    .append("_id", new ObjectId())
                    .append("Name", name)
                    .append("Model", model)
                    .append("Price", price)
                    .append("Country", country));
            System.out.println("Success! Inserted document id: " + result.getInsertedId());
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " + me);
        }
    }
    public static void UpdatePrice (MongoCollection<Document> collection, Bson projectionFields, Scanner sc)
    {
        String name;
        String model;
        int new_price;

        sc.nextLine();
        do {
            System.out.println("Enter name:");
            name = sc.nextLine();

            if (name.isEmpty())
            {
                System.out.println("Invalid, try again");
            }
        }while (name.isEmpty());

        do {
            System.out.println("Enter model:");
            model = sc.nextLine();

            if (model.isEmpty())
            {
                System.out.println("Invalid, try again");
            }
        }while (model.isEmpty());

        Bson filter = collection.find(and(eq("Name", name), eq("Model", model)))
                .projection(projectionFields)
                .first();
        if (filter == null) {
            System.out.println("No results found.");
        }

        do {
            System.out.println("Enter new price:");
            new_price = sc.nextInt();

            if (new_price <= 0)
            {
                System.out.println("Invalid, try again");
            }
        }while (new_price <= 0);

        Bson update = set("Price", new_price);

        collection.updateOne(filter, update);
    }

    public static void DeleteDoc (MongoCollection<Document> collection, Scanner sc)
    {
        String name;
        String model;

        sc.nextLine();
        do {
            System.out.println("Enter name:");
            name = sc.nextLine();

            if (name.isEmpty())
            {
                System.out.println("Invalid, try again");
            }
        }while (name.isEmpty());

        do {
            System.out.println("Enter model:");
            model = sc.nextLine();

            if (model.isEmpty())
            {
                System.out.println("Invalid, try again");
            }
        }while (model.isEmpty());

        Bson doc = and(eq("Name", name), eq("Model", model));

        collection.deleteOne(doc);
    }
}
