package bg.angebotpilot.offer;

import bg.angebotpilot.company.CompanyProfile;
import bg.angebotpilot.customer.Customer;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.librepdf.openpdf.fonts.Liberation;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;

@Service
public class OfferPdfService {

    private static final Color NAVY = new Color(21, 59, 68);
    private static final Color ORANGE = new Color(233, 96, 43);
    private static final Color INK = new Color(29, 41, 53);
    private static final Color MUTED = new Color(102, 118, 133);
    private static final Color LINE = new Color(223, 231, 227);
    private static final Color SOFT = new Color(244, 247, 245);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generate(Offer offer, CompanyProfile profile) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfFonts fonts = createFonts();
            Document document = new Document(PageSize.A4, 46, 46, 58, 54);
            PdfWriter writer = PdfWriter.getInstance(document, output);
            writer.setPageEvent(new FooterEvent(profile, offer, fonts.smallMuted()));

            document.addTitle("Angebot " + offer.getOfferNumber());
            document.addAuthor(profile.getCompanyName());
            document.addSubject(offer.getTitle());
            document.open();

            addHeader(document, profile, fonts);
            addRecipientAndMetadata(document, offer, fonts);
            addTitle(document, offer, fonts);
            addItems(document, offer, fonts);
            addTotals(document, offer, fonts);
            addNotesAndPayment(document, offer, profile, fonts);

            document.close();
            return output.toByteArray();
        } catch (DocumentException | IOException exception) {
            throw new IllegalStateException("PDF офертата не можа да бъде създадена", exception);
        }
    }

    private void addHeader(Document document, CompanyProfile profile, PdfFonts fonts) throws DocumentException {
        PdfPTable header = new PdfPTable(new float[]{3.2f, 1.4f});
        header.setWidthPercentage(100);
        header.setSpacingAfter(24);

        PdfPCell companyCell = plainCell();
        Paragraph company = new Paragraph(profile.getCompanyName(), fonts.companyName());
        company.setLeading(22);
        companyCell.addElement(company);
        addOptionalLine(companyCell, profile.getOwnerName(), fonts.smallMuted());
        addOptionalLine(companyCell, profile.getStreet(), fonts.smallMuted());
        addOptionalLine(companyCell, joinPostalCity(profile.getPostalCode(), profile.getCity()), fonts.smallMuted());
        addOptionalLine(companyCell, profile.getCountry(), fonts.smallMuted());
        header.addCell(companyCell);

        PdfPCell documentType = plainCell();
        documentType.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph brand = new Paragraph("ANGEBOTPILOT", fonts.brand());
        brand.setAlignment(Element.ALIGN_RIGHT);
        brand.setSpacingAfter(6);
        documentType.addElement(brand);
        Paragraph title = new Paragraph("ANGEBOT", fonts.documentTitle());
        title.setAlignment(Element.ALIGN_RIGHT);
        documentType.addElement(title);
        header.addCell(documentType);

        document.add(header);

        PdfPTable accent = new PdfPTable(1);
        accent.setWidthPercentage(100);
        PdfPCell accentCell = new PdfPCell(new Phrase(""));
        accentCell.setFixedHeight(4);
        accentCell.setBorder(Rectangle.NO_BORDER);
        accentCell.setBackgroundColor(NAVY);
        accent.addCell(accentCell);
        accent.setSpacingAfter(22);
        document.add(accent);
    }

    private void addRecipientAndMetadata(Document document, Offer offer, PdfFonts fonts) throws DocumentException {
        PdfPTable section = new PdfPTable(new float[]{1.45f, 1f});
        section.setWidthPercentage(100);
        section.setSpacingAfter(26);

        PdfPCell recipient = plainCell();
        Paragraph label = new Paragraph("ANGEBOT FÜR", fonts.label());
        label.setSpacingAfter(6);
        recipient.addElement(label);

        Customer customer = offer.getCustomer();
        Paragraph customerName = new Paragraph(customer.getDisplayName(), fonts.sectionTitle());
        customerName.setSpacingAfter(3);
        recipient.addElement(customerName);
        if (hasText(customer.getCompanyName()) && hasText(customer.getName())) {
            addOptionalLine(recipient, customer.getName(), fonts.body());
        }
        addOptionalLine(recipient, customer.getStreet(), fonts.body());
        addOptionalLine(recipient, joinPostalCity(customer.getPostalCode(), customer.getCity()), fonts.body());
        addOptionalLine(recipient, customer.getCountry(), fonts.body());
        section.addCell(recipient);

        PdfPCell metadataCell = plainCell();
        PdfPTable metadata = new PdfPTable(new float[]{1.1f, 1f});
        metadata.setWidthPercentage(100);
        addMetadataRow(metadata, "Angebotsnummer", offer.getOfferNumber(), fonts);
        addMetadataRow(metadata, "Datum", DATE_FORMAT.format(offer.getIssueDate()), fonts);
        addMetadataRow(metadata, "Gültig bis", DATE_FORMAT.format(offer.getValidUntil()), fonts);
        metadataCell.addElement(metadata);
        section.addCell(metadataCell);

        document.add(section);
    }

    private void addTitle(Document document, Offer offer, PdfFonts fonts) throws DocumentException {
        Paragraph title = new Paragraph(offer.getTitle(), fonts.offerTitle());
        title.setSpacingAfter(14);
        document.add(title);
    }

    private void addItems(Document document, Offer offer, PdfFonts fonts) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{0.65f, 3.3f, 1.05f, 1.25f, 1.25f});
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingAfter(22);

        addHeaderCell(table, "Pos.", Element.ALIGN_LEFT, fonts);
        addHeaderCell(table, "Beschreibung", Element.ALIGN_LEFT, fonts);
        addHeaderCell(table, "Menge", Element.ALIGN_RIGHT, fonts);
        addHeaderCell(table, "Einzelpreis", Element.ALIGN_RIGHT, fonts);
        addHeaderCell(table, "Gesamt", Element.ALIGN_RIGHT, fonts);

        int position = 1;
        for (OfferItem item : offer.getItems()) {
            boolean shaded = position % 2 == 0;
            addBodyCell(table, String.valueOf(position), Element.ALIGN_LEFT, shaded, fonts.body());
            addBodyCell(table, item.getDescription(), Element.ALIGN_LEFT, shaded, fonts.body());
            addBodyCell(table, formatQuantity(item.getQuantity()) + " " + item.getUnit(),
                    Element.ALIGN_RIGHT, shaded, fonts.body());
            addBodyCell(table, formatMoney(item.getUnitPrice()), Element.ALIGN_RIGHT, shaded, fonts.body());
            addBodyCell(table, formatMoney(item.getSubtotal()), Element.ALIGN_RIGHT, shaded, fonts.bodyBold());
            position++;
        }

        document.add(table);
    }

    private void addTotals(Document document, Offer offer, PdfFonts fonts) throws DocumentException {
        PdfPTable totals = new PdfPTable(new float[]{1.6f, 1f});
        totals.setWidthPercentage(45);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totals.setSpacingAfter(26);

        addTotalRow(totals, "Zwischensumme", formatMoney(offer.getTotalNet()), false, fonts);
        addTotalRow(totals, formatQuantity(offer.getTaxRate()) + " % USt.",
                formatMoney(offer.getTaxAmount()), false, fonts);
        addTotalRow(totals, "Gesamtbetrag", formatMoney(offer.getTotalGross()), true, fonts);
        document.add(totals);
    }

    private void addNotesAndPayment(Document document, Offer offer, CompanyProfile profile, PdfFonts fonts)
            throws DocumentException {
        if (hasText(offer.getNotes())) {
            Paragraph heading = new Paragraph("Anmerkungen", fonts.sectionTitle());
            heading.setSpacingAfter(5);
            document.add(heading);
            Paragraph notes = new Paragraph(offer.getNotes(), fonts.body());
            notes.setLeading(15);
            notes.setSpacingAfter(18);
            document.add(notes);
        }

        PdfPTable paymentBox = new PdfPTable(1);
        paymentBox.setWidthPercentage(100);
        PdfPCell paymentCell = new PdfPCell();
        paymentCell.setPadding(14);
        paymentCell.setBorderColor(LINE);
        paymentCell.setBackgroundColor(SOFT);

        Paragraph paymentHeading = new Paragraph("Zahlungsinformationen", fonts.sectionTitle());
        paymentHeading.setSpacingAfter(6);
        paymentCell.addElement(paymentHeading);

        int days = profile.getPaymentTermDays() == null ? 14 : profile.getPaymentTermDays();
        paymentCell.addElement(new Paragraph(
                "Zahlbar ohne Abzug innerhalb von " + days + " Tagen nach Rechnungsstellung.", fonts.body()));
        addPaymentLine(paymentCell, "IBAN", profile.getIban(), fonts);
        addPaymentLine(paymentCell, "BIC", profile.getBic(), fonts);
        addPaymentLine(paymentCell, "Steuernummer", profile.getTaxNumber(), fonts);
        addPaymentLine(paymentCell, "UID", profile.getVatId(), fonts);
        paymentBox.addCell(paymentCell);
        paymentBox.setSpacingAfter(18);
        document.add(paymentBox);

        if (hasText(profile.getFooterNote())) {
            Paragraph footerNote = new Paragraph(profile.getFooterNote(), fonts.bodyBold());
            footerNote.setAlignment(Element.ALIGN_CENTER);
            document.add(footerNote);
        }
    }

    private void addMetadataRow(PdfPTable table, String label, String value, PdfFonts fonts) {
        PdfPCell labelCell = plainCell(new Phrase(label, fonts.smallMuted()));
        labelCell.setPadding(4);
        PdfPCell valueCell = plainCell(new Phrase(value, fonts.bodyBold()));
        valueCell.setPadding(4);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addHeaderCell(PdfPTable table, String text, int alignment, PdfFonts fonts) {
        PdfPCell cell = new PdfPCell(new Phrase(text, fonts.tableHeader()));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(9);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(NAVY);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, int alignment, boolean shaded, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setPadding(9);
        cell.setBorderWidth(0);
        cell.setBorderWidthBottom(0.5f);
        cell.setBorderColorBottom(LINE);
        if (shaded) {
            cell.setBackgroundColor(SOFT);
        }
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, String value, boolean grandTotal, PdfFonts fonts) {
        Color background = grandTotal ? ORANGE : Color.WHITE;
        Font labelFont = grandTotal ? fonts.totalWhite() : fonts.body();
        Font valueFont = grandTotal ? fonts.totalWhite() : fonts.bodyBold();

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(grandTotal ? 11 : 7);
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setBackgroundColor(background);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(grandTotal ? 11 : 7);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setBackgroundColor(background);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addPaymentLine(PdfPCell cell, String label, String value, PdfFonts fonts) {
        if (!hasText(value)) {
            return;
        }
        Paragraph line = new Paragraph();
        line.setSpacingBefore(3);
        line.add(new Chunk(label + ": ", fonts.bodyBold()));
        line.add(new Chunk(value, fonts.body()));
        cell.addElement(line);
    }

    private void addOptionalLine(PdfPCell cell, String value, Font font) {
        if (hasText(value)) {
            Paragraph line = new Paragraph(value, font);
            line.setLeading(14);
            cell.addElement(line);
        }
    }

    private PdfPCell plainCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(0);
        return cell;
    }

    private PdfPCell plainCell(Phrase phrase) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(0);
        return cell;
    }

    private PdfFonts createFonts() throws IOException {
        Font regular = Liberation.SANS.create(10);
        Font bold = Liberation.SANS_BOLD.create(10);
        return new PdfFonts(regular.getBaseFont(), bold.getBaseFont());
    }

    private String formatMoney(BigDecimal amount) {
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        currency.setCurrency(Currency.getInstance("EUR"));
        return currency.format(amount == null ? BigDecimal.ZERO : amount);
    }

    private String formatQuantity(BigDecimal value) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
        return new DecimalFormat("0.##", symbols).format(value == null ? BigDecimal.ZERO : value);
    }

    private String joinPostalCity(String postalCode, String city) {
        String result = ((postalCode == null ? "" : postalCode.trim()) + " "
                + (city == null ? "" : city.trim())).trim();
        return result.isBlank() ? null : result;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private record PdfFonts(com.lowagie.text.pdf.BaseFont regular,
                            com.lowagie.text.pdf.BaseFont bold) {

        Font companyName() {
            return new Font(bold, 17, Font.NORMAL, NAVY);
        }

        Font documentTitle() {
            return new Font(bold, 25, Font.NORMAL, NAVY);
        }

        Font brand() {
            return new Font(bold, 8, Font.NORMAL, ORANGE);
        }

        Font offerTitle() {
            return new Font(bold, 16, Font.NORMAL, INK);
        }

        Font sectionTitle() {
            return new Font(bold, 11, Font.NORMAL, NAVY);
        }

        Font label() {
            return new Font(bold, 8, Font.NORMAL, ORANGE);
        }

        Font body() {
            return new Font(regular, 9, Font.NORMAL, INK);
        }

        Font bodyBold() {
            return new Font(bold, 9, Font.NORMAL, INK);
        }

        Font smallMuted() {
            return new Font(regular, 8, Font.NORMAL, MUTED);
        }

        Font tableHeader() {
            return new Font(bold, 8, Font.NORMAL, Color.WHITE);
        }

        Font totalWhite() {
            return new Font(bold, 10, Font.NORMAL, Color.WHITE);
        }
    }

    private static class FooterEvent extends PdfPageEventHelper {

        private final String text;
        private final Font font;

        FooterEvent(CompanyProfile profile, Offer offer, Font font) {
            this.text = profile.getCompanyName() + "  |  Angebot " + offer.getOfferNumber();
            this.font = font;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            Phrase footer = new Phrase(text + "  |  Seite " + writer.getPageNumber(), font);
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, footer,
                    PageSize.A4.getWidth() / 2, 26, 0);
        }
    }
}
