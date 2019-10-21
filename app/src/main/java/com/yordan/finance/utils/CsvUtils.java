package com.yordan.finance.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.yordan.finance.model.Expense;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {
    private static File createCSV(File file, List<Expense> expenses){
        List<String> data = new ArrayList<>();

        for(Expense expense : expenses){
            String singleRow = expense.toString();
            data.add(singleRow);
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){

            for(String s: data){
                writer.write(s);
                writer.write("\n");
            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error when creating CSV file.");
        }

        return file;
    }

    public static File generateCSVOnInternal(Context context, List<Expense> expenses) {
        File path = new File(context.getFilesDir(), "exports");
        path.mkdir();
        File file = new File(path, "export.csv");
        return createCSV(file, expenses);
    }
}
