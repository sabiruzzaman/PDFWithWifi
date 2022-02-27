package com.intechnology.pdfwifiprint;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button btn_create_pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_create_pdf = findViewById(R.id.btn_create_pdf);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        btn_create_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createPDFFile(Common.getAppPath(MainActivity.this) + "test_pdf.pdf");
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    private void createPDFFile(String path) {

        if (new File(path).exists())
            new File(path).delete();

        try {
            Document document = new Document();
            // save

            PdfWriter.getInstance(document, new FileOutputStream(path));
            //open to write
            document.open();

            // Setting

            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("IN-Technology");
            document.addCreator("Sabiruzzaman");

            //Font Setting

            BaseColor colorAccent = new BaseColor(0, 153, 204, 255);
            float fontSize = 20.0f;
            float valueFontSize = 26.0f;

            // Custom font

            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

            // Create Title of Document
            Font titleFont = new Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            addNewItem(document, "Order Details", Element.ALIGN_CENTER, titleFont);

            // Add more
            Font orderNumberFont = new Font(fontName, fontSize, Font.NORMAL, colorAccent);
            addNewItem(document, "Order No: ", Element.ALIGN_CENTER, orderNumberFont);

            Font orderNumberValueFont = new Font(fontName, fontSize, Font.NORMAL, BaseColor.BLACK);
            addNewItem(document, "#717171: ", Element.ALIGN_CENTER, orderNumberFont);

            addLineSeparator(document);

            addNewItem(document, "Order Date", Element.ALIGN_LEFT, orderNumberFont);
            addNewItem(document, "27/02/2022", Element.ALIGN_LEFT, orderNumberValueFont);

            addLineSeparator(document);

            addNewItem(document, "Account Name", Element.ALIGN_LEFT, orderNumberFont);
            addNewItem(document, "Sabiruzzaman", Element.ALIGN_LEFT, orderNumberValueFont);

            addLineSeparator(document);

            //Add Product order details
            addLineSpace(document);
            addNewItem(document, "Product Details", Element.ALIGN_CENTER, titleFont);
            addLineSeparator(document);

            //Item
            addNewItemWithLeftAndRight(document, "Pizza 25", "(0.0%)", titleFont, orderNumberValueFont);
            addNewItemWithLeftAndRight(document, "12.0 * 1000", "12000.00", titleFont, orderNumberValueFont);

            addLineSeparator(document);
            //Item2
            addNewItemWithLeftAndRight(document, "Burger 25", "(0.0%)", titleFont, orderNumberValueFont);
            addNewItemWithLeftAndRight(document, "12.0 * 1000", "12000.00", titleFont, orderNumberValueFont);

            // Total

            addLineSpace(document);
            addLineSpace(document);

            addNewItemWithLeftAndRight(document, "Total", "24000.00", titleFont, orderNumberValueFont);

            document.close();

            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

            printPDF();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }


    }

    private void printPDF() {

        PrintManager printManager =(PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(MainActivity.this,Common.getAppPath(MainActivity.this)+"test_pdf.pdf");
            printManager.print("Document",printDocumentAdapter,new PrintAttributes.Builder().build());
        }catch (Exception ex){
            Log.e("TAG", ""+ex.getMessage());
        }

    }

    private void addNewItemWithLeftAndRight(Document document, String textLeft, String textRight, Font textLeftFont, Font textRightFont) throws DocumentException {
        Chunk chunkTextLeft = new Chunk(textLeft, textLeftFont);
        Chunk chunkTextRight = new Chunk(textRight, textRightFont);
        Paragraph p = new Paragraph(chunkTextLeft);
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(chunkTextRight);
        document.add(p);


    }

    private void addLineSeparator(Document document) throws DocumentException {

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }

    private void addNewItem(Document document, String text, int align, Font font) throws DocumentException {

        Chunk chunk = new Chunk(text, font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);
    }
}