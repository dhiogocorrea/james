package dev.dc.james.services;

import dev.dc.james.common.CsvHandler;
import dev.dc.james.common.EmailHandler;
import dev.dc.james.common.TextAreaHandler;
import dev.dc.james.config.EmailConfiguration;
import dev.dc.james.model.Email;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

public class EmailService {

    EmailHandler emailHandler;

    private static final Logger LOG = Logger.getLogger(EmailService.class.getName());

    public static final String UTF8_BOM = "\uFEFF";

    public EmailService(EmailConfiguration emailConfigs) {
        this.emailHandler = new EmailHandler(emailConfigs);
    }

    public void setTextAreaHandler(TextAreaHandler textAreaHandler) {
        LOG.addHandler(textAreaHandler);
    }

    public void readAndSendEmailFromCSV(String csvFilePath, String emailColumnName,
            String subject, String message, String... variableFields) {
        LOG.log(Level.INFO, "Reading csv file...");

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(csvFilePath), Charset.forName("UtF-8"));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error reading csv from {0}", csvFilePath);
        }
        if (lines == null || lines.size() < 2) {
            LOG.log(Level.SEVERE, "Invalid CSV file. Check number of lines.");
            throw new IllegalArgumentException();
        }

        final String delimiter = CsvHandler.detectDelimiter(lines.get(0));
        final String[] headerFields = CsvHandler.extractFields(lines.get(0), delimiter);

        LOG.log(Level.INFO, "Getting email and variabls indexes...");
        final int emailIndex = getIndexOf(headerFields, emailColumnName);
        final List<Integer> variableFieldsIndexes
                = getIndexesOfVariableFields(headerFields, variableFields);

        LOG.log(Level.INFO, "Found {0} emails to send. "
                + "Starting creating emails..", lines.size() - 1);

        lines.parallelStream().skip(1).map(line -> {
            String body = message;

            String[] values = line.split(delimiter);

            String emailAddress = values[emailIndex];

            for (Integer indx : variableFieldsIndexes) {
                String header = removeUTF8BOM(headerFields[indx]);
                String variable = values[indx];

                body = body.replaceAll("((?i)\\{\\{" + header + "\\}\\})", variable);
            }
            return new Email(emailAddress, subject, body);
        }).forEach((Email email) -> {
            try {
                emailHandler.sendEmail(email.getTo(), email.getSubject(),
                        email.getBody());
                LOG.log(Level.INFO, "Successfuly sent email to {0}", email.getTo());
            } catch (MessagingException | UnsupportedEncodingException ex) {
                LOG.log(Level.SEVERE, "FAIL when sending email to {0}", email.getTo());
            }
        });
    }

    private int getIndexOf(String[] array, String value) {
        value = value.toLowerCase().trim();

        for (int i = 0; i < array.length; i++) {
            String header = removeUTF8BOM(array[i].toLowerCase().trim());
            if (header.equals(value)) {
                return i;
            }
        }

        throw new IllegalArgumentException(value + " column does not exists.");
    }

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    private List<Integer> getIndexesOfVariableFields(String[] headerFields, String[] variableFields) {
        List<Integer> variableFieldsIndexes = new ArrayList<>();

        for (String variableField : variableFields) {
            variableFieldsIndexes.add(getIndexOf(headerFields, variableField));
        }

        return variableFieldsIndexes;
    }
}
