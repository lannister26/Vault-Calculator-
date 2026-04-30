package com.calcvault.privatefiles;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.InputType;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

public class MainActivity extends ComponentActivity {
    private static final String PREFS = "vault_prefs";
    private static final String KEY_ITEMS = "items";
    private static final String KEY_PIN_HASH = "pin_hash";
    private static final String KEY_PIN_SALT = "pin_salt";
    private static final String KEY_SETUP_SHOWN = "setup_shown";
    private static final String KEY_ALIAS = "calculator_vault_key";
    private static final int COLOR_BG = Color.rgb(247, 250, 252);
    private static final int COLOR_TEXT = Color.rgb(17, 24, 39);
    private static final int COLOR_MUTED = Color.rgb(82, 96, 111);
    private static final int COLOR_PRIMARY = Color.rgb(15, 118, 110);
    private static final int COLOR_PRIMARY_DARK = Color.rgb(17, 94, 89);
    private static final int COLOR_BUTTON = Color.WHITE;
    private static final int COLOR_FUNCTION = Color.rgb(226, 232, 240);

    private SharedPreferences prefs;
    private TextView display;
    private final StringBuilder expression = new StringBuilder();
    private boolean vaultVisible = false;
    private String pendingRestoreId;
    private ActivityResultLauncher<Intent> pickFilesLauncher;
    private ActivityResultLauncher<Intent> restoreFileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        registerActivityLaunchers();
        registerBackHandler();
        buildCalculator();
        if (!prefs.getBoolean(KEY_SETUP_SHOWN, false)) {
            prefs.edit().putBoolean(KEY_SETUP_SHOWN, true).apply();
            display.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPinDialog(false);
                }
            }, 350);
        }
    }

    private void buildCalculator() {
        vaultVisible = false;
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(22), dp(18), dp(18));
        root.setBackgroundColor(COLOR_BG);

        display = new TextView(this);
        display.setText(expression.length() == 0 ? "0" : expression.toString());
        display.setTextColor(COLOR_TEXT);
        display.setTextSize(42);
        display.setGravity(Gravity.BOTTOM | Gravity.END);
        display.setSingleLine(false);
        display.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        display.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPinDialog(false);
                return true;
            }
        });
        root.addView(display, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setRowCount(5);
        grid.setUseDefaultMargins(false);

        addCalcButton(grid, "C", 0, 0, 1, true);
        addCalcButton(grid, "DEL", 0, 1, 1, true);
        addCalcButton(grid, "%", 0, 2, 1, true);
        addCalcButton(grid, "/", 0, 3, 1, true);
        addCalcButton(grid, "7", 1, 0, 1, false);
        addCalcButton(grid, "8", 1, 1, 1, false);
        addCalcButton(grid, "9", 1, 2, 1, false);
        addCalcButton(grid, "x", 1, 3, 1, true);
        addCalcButton(grid, "4", 2, 0, 1, false);
        addCalcButton(grid, "5", 2, 1, 1, false);
        addCalcButton(grid, "6", 2, 2, 1, false);
        addCalcButton(grid, "-", 2, 3, 1, true);
        addCalcButton(grid, "1", 3, 0, 1, false);
        addCalcButton(grid, "2", 3, 1, 1, false);
        addCalcButton(grid, "3", 3, 2, 1, false);
        addCalcButton(grid, "+", 3, 3, 1, true);
        addCalcButton(grid, "0", 4, 0, 2, false);
        addCalcButton(grid, ".", 4, 2, 1, false);
        addCalcButton(grid, "=", 4, 3, 1, true);

        root.addView(grid, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setContentView(root);
    }

    private void registerBackHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (vaultVisible) {
                    buildCalculator();
                    return;
                }
                ActivityCompat.finishAfterTransition(MainActivity.this);
            }
        });
    }

    private void registerActivityLaunchers() {
        pickFilesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleFilePickerResult(result));
        restoreFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleRestoreResult(result));
    }

    private void addCalcButton(GridLayout grid, final String label, int row, int col, int colSpan, boolean function) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(label.length() > 1 ? 16 : 24);
        button.setTextColor(function ? COLOR_PRIMARY_DARK : COLOR_TEXT);
        button.setAllCaps(false);
        button.setBackground(rounded(function ? COLOR_FUNCTION : COLOR_BUTTON, 18, 0));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCalculatorTap(label);
            }
        });
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                GridLayout.spec(row, 1, 1f),
                GridLayout.spec(col, colSpan, 1f));
        params.width = 0;
        params.height = dp(70);
        params.setMargins(dp(5), dp(5), dp(5), dp(5));
        button.setLayoutParams(params);
        grid.addView(button);
    }

    private void handleCalculatorTap(String label) {
        if ("C".equals(label)) {
            expression.setLength(0);
            updateDisplay();
            return;
        }
        if ("DEL".equals(label)) {
            if (expression.length() > 0) {
                expression.deleteCharAt(expression.length() - 1);
            }
            updateDisplay();
            return;
        }
        if ("=".equals(label)) {
            handleEquals();
            return;
        }
        if (expression.length() >= 60) {
            return;
        }
        expression.append(label);
        updateDisplay();
    }

    private void handleEquals() {
        String raw = expression.toString();
        if (hasPin() && verifyPin(raw)) {
            expression.setLength(0);
            updateDisplay();
            buildVault();
            return;
        }
        if (raw.trim().isEmpty()) {
            return;
        }
        try {
            double value = new ExpressionParser(raw).parse();
            String formatted = formatResult(value);
            expression.setLength(0);
            expression.append(formatted);
            updateDisplay();
        } catch (RuntimeException ex) {
            expression.setLength(0);
            display.setText("Error");
        }
    }

    private void updateDisplay() {
        display.setText(expression.length() == 0 ? "0" : expression.toString());
    }

    private String formatResult(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Invalid result");
        }
        BigDecimal decimal = BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros();
        return decimal.toPlainString();
    }

    private void showPinDialog(final boolean changingExisting) {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(22), dp(8), dp(22), dp(2));

        final EditText pin = new EditText(this);
        pin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pin.setHint("PIN");
        form.addView(pin, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        final EditText confirm = new EditText(this);
        confirm.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        confirm.setHint("Confirm PIN");
        form.addView(confirm, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(changingExisting ? "Change vault PIN" : "Set vault PIN")
                .setMessage("Enter this PIN on the calculator and press = to open the vault.")
                .setView(form)
                .setNegativeButton(changingExisting ? "Cancel" : "Later", null)
                .setPositiveButton("Save", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first = pin.getText().toString().trim();
                String second = confirm.getText().toString().trim();
                if (!first.matches("\\d{4,8}")) {
                    pin.setError("Use 4 to 8 digits");
                    return;
                }
                if (!first.equals(second)) {
                    confirm.setError("PINs do not match");
                    return;
                }
                savePin(first);
                Toast.makeText(MainActivity.this, "PIN saved", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }));
        dialog.show();
    }

    private void buildVault() {
        vaultVisible = true;
        ensureVaultDir();

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(18), dp(18), dp(14));
        page.setBackgroundColor(COLOR_BG);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);

        Button back = compactButton("Calculator");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildCalculator();
            }
        });
        header.addView(back);

        TextView title = new TextView(this);
        title.setText("Private Vault");
        title.setTextColor(COLOR_TEXT);
        title.setTextSize(22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        header.addView(title, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        Button pin = compactButton("PIN");
        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPinDialog(true);
            }
        });
        header.addView(pin);
        page.addView(header, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Button importButton = new Button(this);
        importButton.setText("Import files");
        importButton.setAllCaps(false);
        importButton.setTextSize(18);
        importButton.setTextColor(Color.WHITE);
        importButton.setBackground(rounded(COLOR_PRIMARY, 16, 0));
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportNotice();
            }
        });
        LinearLayout.LayoutParams importParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(56));
        importParams.setMargins(0, dp(18), 0, dp(12));
        page.addView(importButton, importParams);

        ScrollView scroll = new ScrollView(this);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        List<VaultItem> items = loadItems();
        if (items.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No files in vault");
            empty.setTextColor(COLOR_MUTED);
            empty.setTextSize(18);
            empty.setGravity(Gravity.CENTER);
            list.addView(empty, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(160)));
        } else {
            for (VaultItem item : items) {
                list.addView(vaultRow(item));
            }
        }
        scroll.addView(list);
        page.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(page);
    }

    private View vaultRow(final VaultItem item) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(14), dp(12), dp(14), dp(12));
        row.setBackground(rounded(Color.WHITE, 12, Color.rgb(226, 232, 240)));

        TextView name = new TextView(this);
        name.setText(item.name);
        name.setTextColor(COLOR_TEXT);
        name.setTextSize(17);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        row.addView(name);

        TextView meta = new TextView(this);
        meta.setText(humanSize(item.size) + " - " + item.mime);
        meta.setTextColor(COLOR_MUTED);
        meta.setTextSize(13);
        LinearLayout.LayoutParams metaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        metaParams.setMargins(0, dp(3), 0, dp(10));
        row.addView(meta, metaParams);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        Button restore = compactButton("Restore");
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRestore(item);
            }
        });
        actions.addView(restore);

        Button delete = compactButton("Delete");
        delete.setTextColor(Color.rgb(185, 28, 28));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete(item);
            }
        });
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        deleteParams.setMargins(dp(8), 0, 0, 0);
        actions.addView(delete, deleteParams);
        row.addView(actions);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(10));
        row.setLayoutParams(params);
        return row;
    }

    private Button compactButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setTextColor(COLOR_PRIMARY_DARK);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        button.setPadding(dp(12), dp(7), dp(12), dp(7));
        button.setBackground(rounded(Color.WHITE, 12, Color.rgb(203, 213, 225)));
        return button;
    }

    private void showImportNotice() {
        new AlertDialog.Builder(this)
                .setTitle("Import to vault")
                .setMessage("Selected files will be encrypted inside this app. The app will try to remove the originals when Android allows it.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Choose files", (dialog, which) -> openFilePicker())
                .show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        pickFilesLauncher.launch(intent);
    }

    private void startRestore(VaultItem item) {
        pendingRestoreId = item.id;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(item.mime == null || item.mime.trim().isEmpty() ? "application/octet-stream" : item.mime);
        intent.putExtra(Intent.EXTRA_TITLE, item.name);
        restoreFileLauncher.launch(intent);
    }

    private void handleFilePickerResult(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() != RESULT_OK || data == null) {
            return;
        }
        importSelectedFiles(data);
    }

    private void handleRestoreResult(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK && pendingRestoreId != null && data != null && data.getData() != null) {
            restoreSelectedFile(data.getData(), pendingRestoreId);
        }
        pendingRestoreId = null;
    }

    private void importSelectedFiles(Intent data) {
        ArrayList<Uri> uris = new ArrayList<>();
        if (data.getClipData() != null) {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                uris.add(data.getClipData().getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            uris.add(data.getData());
        }

        int encrypted = 0;
        int removed = 0;
        int notRemoved = 0;
        for (Uri uri : uris) {
            try {
                if (importOne(uri)) {
                    removed++;
                } else {
                    notRemoved++;
                }
                encrypted++;
            } catch (Exception ex) {
                Toast.makeText(this, "Could not import one file: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        String message = encrypted + " encrypted";
        if (removed > 0) {
            message += ", " + removed + " originals removed";
        }
        if (notRemoved > 0) {
            message += ", " + notRemoved + " originals need manual deletion";
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        buildVault();
    }

    private boolean importOne(Uri uri) throws Exception {
        takePersistablePermission(uri);
        String name = queryName(uri);
        long size = querySize(uri);
        String mime = getContentResolver().getType(uri);
        if (mime == null || mime.trim().isEmpty()) {
            mime = "application/octet-stream";
        }

        String id = UUID.randomUUID().toString();
        File outFile = itemFile(id);
        SecretKey key = getSecretKey();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();

        try (InputStream input = getContentResolver().openInputStream(uri);
             FileOutputStream rawOut = new FileOutputStream(outFile)) {
            if (input == null) {
                throw new IOException("No input stream");
            }
            rawOut.write(iv.length);
            rawOut.write(iv);
            try (CipherOutputStream encryptedOut = new CipherOutputStream(rawOut, cipher)) {
                copy(input, encryptedOut);
            }
        }

        List<VaultItem> items = loadItems();
        items.add(new VaultItem(id, name, mime, size, System.currentTimeMillis()));
        saveItems(items);
        return tryDeleteOriginal(uri);
    }

    private void restoreSelectedFile(Uri destination, String itemId) {
        VaultItem item = findItem(itemId);
        if (item == null) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            decryptToUri(item, destination);
            Toast.makeText(this, "File restored", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Restore failed: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void decryptToUri(VaultItem item, Uri destination) throws Exception {
        File file = itemFile(item.id);
        try (FileInputStream fileIn = new FileInputStream(file)) {
            int ivLength = fileIn.read();
            if (ivLength <= 0 || ivLength > 32) {
                throw new IOException("Invalid encrypted file");
            }
            byte[] iv = new byte[ivLength];
            readFully(fileIn, iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new GCMParameterSpec(128, iv));
            try (CipherInputStream decryptedIn = new CipherInputStream(fileIn, cipher);
                 OutputStream output = getContentResolver().openOutputStream(destination, "w")) {
                if (output == null) {
                    throw new IOException("No output stream");
                }
                copy(decryptedIn, output);
            }
        }
    }

    private void confirmDelete(final VaultItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete from vault")
                .setMessage("This permanently deletes the encrypted copy.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> deleteVaultItem(item))
                .show();
    }

    private void deleteVaultItem(VaultItem item) {
        File file = itemFile(item.id);
        if (file.exists() && !file.delete()) {
            Toast.makeText(this, "Could not delete encrypted file", Toast.LENGTH_LONG).show();
            return;
        }
        List<VaultItem> items = loadItems();
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).id.equals(item.id)) {
                items.remove(i);
            }
        }
        saveItems(items);
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        buildVault();
    }

    private SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            KeyGenerator generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build();
            generator.init(spec);
            return generator.generateKey();
        }
        return (SecretKey) keyStore.getKey(KEY_ALIAS, null);
    }

    private void takePersistablePermission(Uri uri) {
        try {
            int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            getContentResolver().takePersistableUriPermission(uri, flags);
        } catch (Exception ignored) {
            // Some providers grant temporary access only. The app copies the file immediately.
        }
    }

    private boolean tryDeleteOriginal(Uri uri) {
        try {
            if (DocumentsContract.isDocumentUri(this, uri)) {
                return DocumentsContract.deleteDocument(getContentResolver(), uri);
            }
        } catch (Exception ignored) {
            return false;
        }
        return false;
    }

    private String queryName(Uri uri) {
        String name = null;
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    name = cursor.getString(index);
                }
            }
        }
        if (name == null || name.trim().isEmpty()) {
            name = "file-" + System.currentTimeMillis();
        }
        return name;
    }

    private long querySize(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (index >= 0 && !cursor.isNull(index)) {
                    return cursor.getLong(index);
                }
            }
        }
        return 0L;
    }

    private List<VaultItem> loadItems() {
        ArrayList<VaultItem> items = new ArrayList<>();
        String raw = prefs.getString(KEY_ITEMS, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                items.add(new VaultItem(
                        obj.getString("id"),
                        obj.optString("name", "file"),
                        obj.optString("mime", "application/octet-stream"),
                        obj.optLong("size", 0L),
                        obj.optLong("createdAt", 0L)));
            }
        } catch (JSONException ignored) {
            prefs.edit().putString(KEY_ITEMS, "[]").apply();
        }
        return items;
    }

    private void saveItems(List<VaultItem> items) {
        JSONArray array = new JSONArray();
        for (VaultItem item : items) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id", item.id);
                obj.put("name", item.name);
                obj.put("mime", item.mime);
                obj.put("size", item.size);
                obj.put("createdAt", item.createdAt);
                array.put(obj);
            } catch (JSONException ignored) {
                // JSONObject only fails for invalid numbers; these values are app generated.
            }
        }
        prefs.edit().putString(KEY_ITEMS, array.toString()).apply();
    }

    private VaultItem findItem(String id) {
        for (VaultItem item : loadItems()) {
            if (item.id.equals(id)) {
                return item;
            }
        }
        return null;
    }

    private File ensureVaultDir() {
        File dir = new File(getFilesDir(), "vault");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            new File(dir, ".nomedia").createNewFile();
        } catch (IOException ignored) {
            // App-private storage is already hidden from media scanners.
        }
        return dir;
    }

    private File itemFile(String id) {
        return new File(ensureVaultDir(), id + ".bin");
    }

    private void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
    }

    private void readFully(InputStream input, byte[] buffer) throws IOException {
        int offset = 0;
        while (offset < buffer.length) {
            int read = input.read(buffer, offset, buffer.length - offset);
            if (read == -1) {
                throw new IOException("Unexpected end of file");
            }
            offset += read;
        }
    }

    private String humanSize(long bytes) {
        if (bytes <= 0) {
            return "Unknown size";
        }
        String[] units = {"B", "KB", "MB", "GB"};
        double value = bytes;
        int index = 0;
        while (value >= 1024 && index < units.length - 1) {
            value /= 1024;
            index++;
        }
        return BigDecimal.valueOf(value).setScale(index == 0 ? 0 : 1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + " " + units[index];
    }

    private boolean hasPin() {
        return prefs.contains(KEY_PIN_HASH) && prefs.contains(KEY_PIN_SALT);
    }

    private void savePin(String pin) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        String saltEncoded = Base64.encodeToString(salt, Base64.NO_WRAP);
        String hash = hashPin(pin, saltEncoded);
        prefs.edit()
                .putString(KEY_PIN_SALT, saltEncoded)
                .putString(KEY_PIN_HASH, hash)
                .apply();
    }

    private boolean verifyPin(String pin) {
        String salt = prefs.getString(KEY_PIN_SALT, "");
        String expected = prefs.getString(KEY_PIN_HASH, "");
        if (salt.isEmpty() || expected.isEmpty()) {
            return false;
        }
        byte[] actualBytes = Base64.decode(hashPin(pin, salt), Base64.NO_WRAP);
        byte[] expectedBytes = Base64.decode(expected, Base64.NO_WRAP);
        return MessageDigest.isEqual(actualBytes, expectedBytes);
    }

    private String hashPin(String pin, String saltEncoded) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.decode(saltEncoded, Base64.NO_WRAP));
            return Base64.encodeToString(digest.digest(pin.getBytes("UTF-8")), Base64.NO_WRAP);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not hash PIN", ex);
        }
    }

    private GradientDrawable rounded(int fill, int radiusDp, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radiusDp));
        if (strokeColor != 0) {
            drawable.setStroke(dp(1), strokeColor);
        }
        return drawable;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static final class VaultItem {
        final String id;
        final String name;
        final String mime;
        final long size;
        final long createdAt;

        VaultItem(String id, String name, String mime, long size, long createdAt) {
            this.id = id;
            this.name = name;
            this.mime = mime;
            this.size = size;
            this.createdAt = createdAt;
        }
    }

    private static final class ExpressionParser {
        private final String input;
        private int pos = -1;
        private int ch;

        ExpressionParser(String input) {
            this.input = input.replace("x", "*");
            nextChar();
        }

        double parse() {
            double result = parseExpression();
            if (pos < input.length()) {
                throw new IllegalArgumentException("Unexpected character");
            }
            return result;
        }

        private void nextChar() {
            ch = (++pos < input.length()) ? input.charAt(pos) : -1;
        }

        private boolean eat(int charToEat) {
            while (ch == ' ') {
                nextChar();
            }
            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }

        private double parseExpression() {
            double x = parseTerm();
            while (true) {
                if (eat('+')) {
                    x += parseTerm();
                } else if (eat('-')) {
                    x -= parseTerm();
                } else {
                    return x;
                }
            }
        }

        private double parseTerm() {
            double x = parseFactor();
            while (true) {
                if (eat('*')) {
                    x *= parseFactor();
                } else if (eat('/')) {
                    x /= parseFactor();
                } else if (eat('%')) {
                    x %= parseFactor();
                } else {
                    return x;
                }
            }
        }

        private double parseFactor() {
            if (eat('+')) {
                return parseFactor();
            }
            if (eat('-')) {
                return -parseFactor();
            }
            double x;
            int start = pos;
            if (eat('(')) {
                x = parseExpression();
                if (!eat(')')) {
                    throw new IllegalArgumentException("Missing closing parenthesis");
                }
            } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                while ((ch >= '0' && ch <= '9') || ch == '.') {
                    nextChar();
                }
                x = Double.parseDouble(input.substring(start, pos));
            } else {
                throw new IllegalArgumentException("Unexpected character");
            }
            return x;
        }
    }
}
