package com.location.contrats.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class SimplePdf {
    private SimplePdf() {}

    static byte[] fromLines(List<String> lines) {
        StringBuilder content = new StringBuilder("BT /F1 12 Tf 50 780 Td\n");
        for (String raw : lines) {
            String line = raw.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
            content.append("(").append(line).append(") Tj\n0 -18 Td\n");
        }
        content.append("ET\n");
        byte[] stream = content.toString().getBytes(StandardCharsets.ISO_8859_1);
        List<String> objs = new ArrayList<>();
        objs.add("<< /Type /Catalog /Pages 2 0 R >>");
        objs.add("<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
        objs.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>");
        objs.add("<< /Length " + stream.length + " >>\nstream\n" + content + "endstream");
        objs.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write("%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1));
            int[] xref = new int[objs.size() + 1];
            xref[0] = 0;
            for (int i = 0; i < objs.size(); i++) {
                xref[i + 1] = out.size();
                out.write((i + 1 + " 0 obj\n").getBytes(StandardCharsets.ISO_8859_1));
                out.write(objs.get(i).getBytes(StandardCharsets.ISO_8859_1));
                out.write("\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));
            }
            int startxref = out.size();
            out.write(("xref\n0 " + (objs.size() + 1) + "\n").getBytes(StandardCharsets.ISO_8859_1));
            out.write("0000000000 65535 f \n".getBytes(StandardCharsets.ISO_8859_1));
            for (int i = 1; i <= objs.size(); i++) {
                out.write(String.format("%010d 00000 n \n", xref[i]).getBytes(StandardCharsets.ISO_8859_1));
            }
            out.write(("trailer << /Size " + (objs.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + startxref + "\n%%EOF\n")
                    .getBytes(StandardCharsets.ISO_8859_1));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return out.toByteArray();
    }
}
