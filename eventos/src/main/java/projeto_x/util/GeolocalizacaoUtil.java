package projeto_x.util;

public class GeolocalizacaoUtil {
    private static final int RAIO_TERRA_KM = 6371; // Raio médio da Terra em km

    /**
     * Calcula a distância entre dois pontos de latitude e longitude usando a fórmula de Haversine.
     * 
     * @param lat1 Latitude do primeiro ponto
     * @param lon1 Longitude do primeiro ponto
     * @param lat2 Latitude do segundo ponto
     * @param lon2 Longitude do segundo ponto
     * @return Distancia entre os pontos em quilômetros.
     */
    public static Double calcularDistanciaHaversine(Double lat1, Double lon1, Double lat2, Double lon2) {
        // Validação de cada ponto de latitude longitude
        if (lat1 != null && (lat1 < -90.0 || lat1 > 90.0) || lat2 != null && (lat2 < -90.0 || lat2 > 90.0)) {
            throw new IllegalArgumentException("Latitude inválida: deve estar entre -90 e +90!");
        }

        if (lon1 != null && (lon1 < -180.0 || lon1 > 180.0) || lon2 != null && (lon2 < 180.0 || lon2 > 180.0)) {
            throw new IllegalArgumentException("Longitude inválida: deve estar entre -180 e +180!");
        }

        // Converte graus para radianos
        Double lat1Rad = (Double) Math.toRadians(lat1);
        Double lon1Rad = (Double) Math.toRadians(lon1);
        Double lat2Rad = (Double) Math.toRadians(lat2);
        Double lon2Rad = (Double) Math.toRadians(lon2);

        // Diferença de coordenadas
        Double dlat = (Double) lat2Rad - lat1Rad;
        Double dLon = (Double) lon2Rad - lon1Rad;

        // Fórmula de Haversine
        Double a = (Double) Math.pow(Math.sin(dlat / 2), 2) +
                            Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                            Math.pow(Math.sin(dLon / 2), 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAIO_TERRA_KM * c; // Distância em quilômetros
    }
}