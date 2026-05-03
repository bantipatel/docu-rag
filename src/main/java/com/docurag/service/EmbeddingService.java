package com.docurag.service;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {
    private static final int DIM = 512;

    public float[] embed(String text) {
        float[] vector = new float[DIM];
        String[] tokens = text.toLowerCase().replaceAll("[^a-z0-9\\s]", "").split("\\s+");
        for (String token : tokens) {
            if (token.isBlank()) continue;
            int idx = Math.abs(token.hashCode()) % DIM;
            vector[idx] += 1.0f;
            // bigram
            if (token.length() > 3) {
                int idx2 = Math.abs((token + "_") .hashCode()) % DIM;
                vector[idx2] += 0.5f;
            }
        }
        return normalize(vector);
    }

    public float cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) return 0f;
        float dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return 0f;
        return dot / (float)(Math.sqrt(normA) * Math.sqrt(normB));
    }

    private float[] normalize(float[] v) {
        float norm = 0;
        for (float x : v) norm += x * x;
        norm = (float) Math.sqrt(norm);
        if (norm == 0) return v;
        float[] result = new float[v.length];
        for (int i = 0; i < v.length; i++) result[i] = v[i] / norm;
        return result;
    }
}
