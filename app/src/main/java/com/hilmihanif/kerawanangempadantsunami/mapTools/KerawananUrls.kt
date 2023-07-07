package com.hilmihanif.kerawanangempadantsunami.mapTools

object KerawananUrls {
    val gempa: Map<String, String> = mapOf(
        "Aceh" to "https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/krb_gempa_aceh/FeatureServer/0",
        "Sumatera Utara" to "https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/krb_gempa_sumatera_utara/FeatureServer/0",
        "Sumatera Barat" to "https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/krb_gempa_sumatera_barat/FeatureServer/0",
        "Riau" to "https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/krb_gempa_riau/FeatureServer/0",
        "" to ""
    )
    val gerakanTanah: Map<String, String> = mapOf(
        "Aceh" to "https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/zkgt_aceh/FeatureServer/0",
        "Sumatera Utara" to "https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/zkgt_sumatera_utara/FeatureServer/0",
        "Sumatera Barat" to "https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/zkgt_sumatera_barat/FeatureServer/0",
        "Riau" to "",
        "" to ""
    )
    val tsunami: Map<String, String> = mapOf(
        "Aceh" to "https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/krb_tsunami_aceh/FeatureServer/0",
        "Sumatera Utara" to "https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/krb_tsunami_sumatera_utara/FeatureServer/0",
        "Sumatera Barat" to "https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/krb_tsunami_sumatera_barat/FeatureServer/0",
        "Riau" to "",
        "" to ""
    )
}