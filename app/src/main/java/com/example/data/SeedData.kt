package com.example.data

object SeedData {
    val initialNodes: List<TechNode> = listOf(
        TechNode(
            id = "fire",
            title = "Fuego Primitivo",
            description = "Iniciación de combustión exotérmica por fricción o percusión de pirita y pedernal.",
            category = ScienceCategory.PHYSICS,
            requiredNodes = emptyList(),
            resourceCosts = mapOf("Madera" to 5),
            x = 100f,
            y = 200f,
            scientificPrinciple = "La combustión es una reacción redox exotérmica rápida entre un combustible (celulosa/lignina) y un oxidante (O₂ atmosférico), superando la energía de activación por fricción cinética.",
            formulaOrEquation = "(C₆H₁₀O₅)ₙ + 6n O₂ → 6n CO₂ + 5n H₂O + ΔH (-418 kJ/mol)",
            technicalData = mapOf(
                "Temperatura de ignición" to "300°C",
                "Llama abierta básica" to "600°C - 800°C",
                "Oxidante requerido" to "21% O₂ atmosférico"
            ),
            targetTemperature = 650f,
            toleranceRange = 50f,
            isUnlocked = true, // Base node unlocked initially
            srsInterval = 1,
            srsEaseFactor = 2.5f,
            srsRepetitions = 1,
            nextReviewDate = System.currentTimeMillis() + 86400000L
        ),
        TechNode(
            id = "clay",
            title = "Recolección de Arcilla",
            description = "Identificación de aluminosilicatos hidratados en sedimentos fluviales y decantación de impurezas.",
            category = ScienceCategory.EARTH,
            requiredNodes = emptyList(),
            resourceCosts = mapOf("Madera" to 2),
            x = 100f,
            y = 500f,
            scientificPrinciple = "La arcilla está compuesta por filosilicatos hidratados (caolinita Al₂Si₂O₅(OH)₄). Su plasticidad se debe a películas de agua adsorbida entre láminas microscópicas de tetraedros de sílice y octaedros de alúmina.",
            formulaOrEquation = "Al₂O₃ · 2SiO₂ · 2H₂O (Caolinita estructural)",
            technicalData = mapOf(
                "Tamaño de partícula" to "< 2 micrómetros",
                "Comportamiento" to "Tixotrópico y plástico con agua",
                "Pérdida de agua estructural" to "450°C - 600°C"
            ),
            targetTemperature = 100f,
            toleranceRange = 20f,
            isUnlocked = true, // Base node unlocked initially
            srsInterval = 1,
            srsEaseFactor = 2.5f,
            srsRepetitions = 1,
            nextReviewDate = System.currentTimeMillis() + 86400000L
        ),
        TechNode(
            id = "charcoal",
            title = "Horno de Carbón Vegetal",
            description = "Pirólisis de biomasa leñosa en atmósfera anóxica para concentrar carbono puro.",
            category = ScienceCategory.CHEMISTRY,
            requiredNodes = listOf("fire"),
            resourceCosts = mapOf("Madera" to 20),
            x = 350f,
            y = 120f,
            scientificPrinciple = "Al calentar madera en ausencia de oxígeno por encima de 400°C, se descomponen los volátiles (alquitranes, metanol, agua) dejando una matriz porosa de más del 85% de carbono elemental de alto poder calorífico.",
            formulaOrEquation = "Biomasa + Calor (anóxico) → C (sólido) + CO + CH₄ + Vapores orgánicos",
            technicalData = mapOf(
                "Poder calorífico" to "30 MJ/kg (vs 15 MJ/kg de madera)",
                "Temperatura de pirólisis" to "450°C - 550°C",
                "Eficiencia térmica" to "Doble de temperatura con tiro forzado"
            ),
            targetTemperature = 500f,
            toleranceRange = 35f,
            isUnlocked = false
        ),
        TechNode(
            id = "mud_kiln",
            title = "Horno de Barro Refractario",
            description = "Cámara de combustión cerrada con tiro natural para sintetizar cerámica e insular calor extremo.",
            category = ScienceCategory.EARTH,
            requiredNodes = listOf("fire", "clay"),
            resourceCosts = mapOf("Madera" to 15, "Arcilla" to 30),
            x = 350f,
            y = 350f,
            scientificPrinciple = "A 900°C-1000°C, los silicatos sufren vitrificación incipiente: el cuarzo y los fundentes reaccionan para formar una fase vítrea líquida que sella los poros al enfriarse, transformando el barro quebradizo en cerámica dura e impermeable.",
            formulaOrEquation = "Al₂Si₂O₅(OH)₄ + calor → SiAl₂O₅ (Metacaolín) → Cerámica vítrea",
            technicalData = mapOf(
                "Temperatura operativa" to "850°C - 1050°C",
                "Efecto Venturi" to "Aceleración de gases en la tobera",
                "Resistencia a choque térmico" to "Alta con desgrasante de cuarzo"
            ),
            targetTemperature = 920f,
            toleranceRange = 45f,
            isUnlocked = false
        ),
        TechNode(
            id = "copper_smelting",
            title = "Fundición de Cobre",
            description = "Reducción carbotérmica de minerales oxidados y carbonatados de cobre (malaquita) en crisol.",
            category = ScienceCategory.CHEMISTRY,
            requiredNodes = listOf("charcoal", "mud_kiln"),
            resourceCosts = mapOf("Carbón" to 25, "Arcilla" to 15, "Malaquita" to 10),
            x = 620f,
            y = 200f,
            scientificPrinciple = "El monóxido de carbono (CO) generado por la combustión incompleta del carbón actúa como agente reductor a temperaturas superiores al punto de fusión del cobre (1085°C), reduciendo el ion Cu²⁺ a metal elemental líquido.",
            formulaOrEquation = "Cu₂CO₃(OH)₂ + 2CO → 2Cu (líquido) + 3CO₂ + H₂O",
            technicalData = mapOf(
                "Punto de fusión del Cobre" to "1085°C",
                "Agente reductor" to "Monóxido de Carbono (CO)",
                "Poder reductor de Ellingham" to "ΔG < 0 por encima de 600°C"
            ),
            targetTemperature = 1085f,
            toleranceRange = 30f,
            isUnlocked = false
        ),
        TechNode(
            id = "bellows_forge",
            title = "Fuelle y Forja Mecánica",
            description = "Inyección forzada de oxígeno a presión para elevar la combustión más allá de los 1200°C.",
            category = ScienceCategory.MECHANICS,
            requiredNodes = listOf("mud_kiln"),
            resourceCosts = mapOf("Madera" to 25, "Cobre" to 10),
            x = 620f,
            y = 480f,
            scientificPrinciple = "Ley de fluidos y estequiometría de combustión forzada: aumentar el flujo másico de aire (ṁ) incrementa la velocidad de reacción cinética y el transporte de oxígeno hacia el núcleo del carbón incandescente.",
            formulaOrEquation = "Q_combustión = ṁ_combustible · Poder_Calorífico · η_oxígeno",
            technicalData = mapOf(
                "Presión estática generada" to "0.15 - 0.30 bar",
                "Temperatura alcanzable" to "Hasta 1350°C",
                "Mecanismo" to "Válvula unidireccional de cuero/madera"
            ),
            targetTemperature = 1200f,
            toleranceRange = 40f,
            isUnlocked = false
        ),
        TechNode(
            id = "bronze_alloy",
            title = "Metalurgia del Bronce",
            description = "Aleación sustitucional de 88% Cobre con 12% Estaño para una matriz metálica dúctil y tenaz.",
            category = ScienceCategory.MECHANICS,
            requiredNodes = listOf("copper_smelting", "bellows_forge"),
            resourceCosts = mapOf("Cobre" to 30, "Carbón" to 20, "Estaño" to 10),
            x = 900f,
            y = 200f,
            scientificPrinciple = "La introducción de átomos de estaño (radio atómico 140 pm) en la red cristalina FCC del cobre (radio 128 pm) genera tensiones internas que bloquean el deslizamiento de dislocaciones, incrementando drásticamente la dureza y reduciendo el punto de fusión a ~950°C.",
            formulaOrEquation = "Cu (88 wt%) + Sn (12 wt%) → Solución sólida α + fase δ (Cu₃₁Sn₈)",
            technicalData = mapOf(
                "Dureza Brinell" to "120 HB (vs 35 HB del cobre puro)",
                "Punto de fusión rebajado" to "~950°C",
                "Resistencia a corrosión" to "Capa pasivante de dióxido de estaño"
            ),
            targetTemperature = 980f,
            toleranceRange = 25f,
            isUnlocked = false
        ),
        TechNode(
            id = "distillation",
            title = "Alambique y Destilación",
            description = "Separación por diferencias de volatilidad y presión de vapor para purificar alcohol y ácidos.",
            category = ScienceCategory.CHEMISTRY,
            requiredNodes = listOf("copper_smelting"),
            resourceCosts = mapOf("Cobre" to 20, "Madera" to 15, "Agua" to 10),
            x = 900f,
            y = 420f,
            scientificPrinciple = "Ley de Raoult y equilibrio líquido-vapor: al calentar una mezcla binaria, la fase de vapor se enriquece en el componente más volátil (menor punto de ebullición). El condensador de cobre refrigerado retira calor latente (ΔH_vap).",
            formulaOrEquation = "P_total = P_A* · x_A + P_B* · x_B",
            technicalData = mapOf(
                "Punto ebullición etanol" to "78.37°C",
                "Punto ebullición agua" to "100.0°C",
                "Conductividad térmica cobre" to "401 W/(m·K)"
            ),
            targetTemperature = 85f,
            toleranceRange = 5f,
            isUnlocked = false
        ),
        TechNode(
            id = "voltaic_pile",
            title = "Pila Voltaica",
            description = "Generación electroquímica continua de potencial eléctrico apilando discos de Cu y Zn con electrolito.",
            category = ScienceCategory.PHYSICS,
            requiredNodes = listOf("bronze_alloy", "distillation"),
            resourceCosts = mapOf("Cobre" to 25, "Zinc" to 15, "Ácido" to 10),
            x = 1180f,
            y = 150f,
            scientificPrinciple = "Celdas galvánicas acopladas en serie: la diferencia de potencial electroquímico estándar entre electrodos impulsa un flujo espontáneo de electrones del ánodo oxidante al cátodo reductor a través del circuito externo.",
            formulaOrEquation = "Ánodo: Zn → Zn²⁺ + 2e⁻ (E° = +0.76V) | Cátodo: 2H⁺ + 2e⁻ → H₂ (E° = 0.0V)",
            technicalData = mapOf(
                "Voltaje por celda" to "~0.76 - 1.10 V",
                "Electrolito" to "Salmuera o solución de ácido sulfúrico diluido",
                "Potencial de Nernst" to "E = E° - (RT/nF) · ln(Q)"
            ),
            targetTemperature = 25f,
            toleranceRange = 10f,
            isUnlocked = false
        ),
        TechNode(
            id = "faraday_induction",
            title = "Inducción Electromagnética",
            description = "Conversión de energía mecánica a corriente eléctrica mediante flujo magnético variable en solenoide.",
            category = ScienceCategory.PHYSICS,
            requiredNodes = listOf("voltaic_pile"),
            resourceCosts = mapOf("Cobre" to 40, "Hierro" to 20, "Madera" to 10),
            x = 1450f,
            y = 150f,
            scientificPrinciple = "Ley de Faraday-Lenz: una variación temporal en el flujo magnético (Φ_B) que atraviesa un circuito cerrado induce una fuerza electromotriz (FEM) que produce una corriente cuyo campo magnético se opone al cambio original.",
            formulaOrEquation = "ε = -N · (dΦ_B / dt) = -N · d(B · A · cos θ)/dt",
            technicalData = mapOf(
                "Bobinado requerido" to "Hilo de cobre esmaltado aislado",
                "Rendimiento mecánico" to "Ley de Biot-Savart & Fuerzas de Lorentz",
                "Salida" to "Corriente Alterna senoidal"
            ),
            targetTemperature = 40f,
            toleranceRange = 15f,
            isUnlocked = false
        ),
        TechNode(
            id = "astrolabe",
            title = "Astrolabio y Triangulación",
            description = "Instrumento de cálculo trigonométrico para fijar latitud, tiempo astronómico y orientación geodésica.",
            category = ScienceCategory.MATH,
            requiredNodes = listOf("bronze_alloy"),
            resourceCosts = mapOf("Bronce" to 20, "Madera" to 10),
            x = 1180f,
            y = 380f,
            scientificPrinciple = "Proyección estereográfica de la esfera celeste sobre un plano ecuatorial, conservando ángulos y círculos. Permite resolver la altitud de cuerpos celestes mediante el teorema del seno esférico.",
            formulaOrEquation = "sen(a) = sen(φ)·sen(δ) + cos(φ)·cos(δ)·cos(H)",
            technicalData = mapOf(
                "Precisión angular" to "Graduación en nonios (0.25°)",
                "Placa base (Tímpano)" to "Grabado para latitud geográfica local",
                "Alidada" to "Regla de mira colimada"
            ),
            targetTemperature = 20f,
            toleranceRange = 10f,
            isUnlocked = false
        ),
        TechNode(
            id = "penicillin",
            title = "Aislamiento de Antibióticos",
            description = "Cultivo selectivo de hongo Penicillium en medio glucósido para inhibir síntesis de pared bacteriana.",
            category = ScienceCategory.BIOLOGY,
            requiredNodes = listOf("distillation"),
            resourceCosts = mapOf("Vidrio" to 10, "Extracto" to 15, "Carbón" to 10),
            x = 1180f,
            y = 560f,
            scientificPrinciple = "El anillo betalactámico de la penicilina actúa como análogo estructural del sustrato D-Ala-D-Ala, inhibiendo irreversiblemente la enzima transpeptidasa que entrecruza las cadenas de peptidoglicano de la pared bacteriana Gram-positiva.",
            formulaOrEquation = "Inhibición competitiva suicida: Enzima-Transpeptidasa + Betalactámico → Complejo inactivo covalente",
            technicalData = mapOf(
                "pH óptimo de cultivo" to "6.0 - 6.5 a 24°C",
                "Filtro bacteriológico" to "Porcelana porosa o carbón activado",
                "Efecto" to "Lisis osmótica bacteriana selectiva"
            ),
            targetTemperature = 24f,
            toleranceRange = 3f,
            isUnlocked = false
        )
    )

    val initialResources = mapOf(
        "Madera" to 60,
        "Arcilla" to 50,
        "Carbón" to 20,
        "Malaquita" to 15,
        "Cobre" to 5,
        "Estaño" to 10,
        "Bronce" to 0,
        "Agua" to 30,
        "Zinc" to 15,
        "Ácido" to 10,
        "Hierro" to 20,
        "Vidrio" to 10,
        "Extracto" to 15
    )
}
