package DbConnection;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class BarcodeService {

    private BarcodeService() {} // prevent instantiation

    private static final SecureRandom RNG = new SecureRandom();
    private static final int MAX_ATTEMPTS = 50;

    /**
     * Generates a unique barcode not yet existing in products.barcode.
     * Format: yyMMdd + 7 random digits (13 digits total).
     */
    public static String generateUniqueBarcode(Connection con) throws SQLException {
        if (con == null) throw new IllegalArgumentException("Connection is null");

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            String barcode = generateBarcode13();
            if (!barcodeExists(con, barcode)) {
                return barcode;
            }
        }

        // If we got here, something unusual happened (very unlikely)
        throw new SQLException("Failed to generate a unique barcode after " + MAX_ATTEMPTS + " attempts.");
    }

    // --- Internal generator (13 digits) ---
    private static String generateBarcode13() {
        String datePart = new SimpleDateFormat("yyMMdd").format(new Date()); // 6 digits
        String randPart = String.format("%07d", RNG.nextInt(10_000_000));    // 7 digits
        return datePart + randPart; // 13 digits
    }

    // --- DB check ---
    private static boolean barcodeExists(Connection con, String barcode) throws SQLException {
        final String sql = "SELECT 1 FROM products WHERE barcode = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}