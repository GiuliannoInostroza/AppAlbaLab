package com.example.albalab.data.repository

import com.example.albalab.data.model.Programa

// Simula el origen de datos de los programas (más adelante podría venir de una API)
class ProgramaRepository {

    fun obtenerProgramas(): List<Programa> {
        return listOf(
            Programa(
                id = 1,
                nombre = "Preparación para el empleo",
                categoria = "Programa",
                descripcion = "Talleres donde jóvenes y adultos con discapacidad múltiple desarrollan rutinas de trabajo, comunicación y autonomía.",
                destinoAporte = "Materiales de taller y monitores de apoyo."
            ),
            Programa(
                id = 2,
                nombre = "Escuela Especial Jan Van Dijk",
                categoria = "Programa",
                descripcion = "Educación especializada para estudiantes con discapacidad múltiple, con foco en comunicación y vida independiente.",
                destinoAporte = "Recursos pedagógicos y adaptaciones de aula."
            ),
            Programa(
                id = 3,
                nombre = "Red Sociolaboral Alba Lab",
                categoria = "Programa",
                descripcion = "Conecta a los participantes con empresas y acompaña su adaptación al puesto de trabajo.",
                destinoAporte = "Seguimiento laboral y apoyo a los equipos de las empresas."
            ),
            Programa(
                id = 4,
                nombre = "Productos de los talleres",
                categoria = "Producto",
                descripcion = "Artículos elaborados por los participantes de los talleres de producción.",
                destinoAporte = "Insumos y continuidad de los talleres."
            ),
            Programa(
                id = 5,
                nombre = "Regalos corporativos",
                categoria = "Producto",
                descripcion = "Packs para empresas en fechas especiales, preparados en los talleres.",
                destinoAporte = "Insumos y apoyo a los participantes."
            ),
            Programa(
                id = 6,
                nombre = "Voluntariado corporativo",
                categoria = "Servicio RSE",
                descripcion = "Jornadas en que equipos de una empresa comparten actividades con los estudiantes.",
                destinoAporte = "Actividades recreativas y de integración."
            ),
            Programa(
                id = 7,
                nombre = "Capacitación en inclusión",
                categoria = "Servicio RSE",
                descripcion = "Charlas para empresas sobre cómo incluir a personas con discapacidad en sus equipos.",
                destinoAporte = "Financia los programas de la organización."
            )
        )
    }
}
