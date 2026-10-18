# Alba Lab Conecta - AppAlbaLab_Grupo1

Aplicación Android para el caso **CORPALIV / Alba Lab** (DSY1105 - Desarrollo de Aplicaciones Móviles, Evaluación Parcial 2).

CORPALIV promueve la inclusión social y laboral de personas con discapacidad múltiple. Quien quiere colaborar encuentra la información repartida en muchos canales. Esta app reúne el propósito de la organización, sus programas y el destino de los aportes, y termina en un formulario para concretar la colaboración (donación, voluntariado, alianza, compra o capacitación).

## Integrantes

- Giulianno Inostroza
- Pascal Pacheco
- naperezg

## Funcionalidades

- **Login y registro** con validación por campo. La contraseña se guarda cifrada (hash SHA-256) en Room.
- **Sesión persistente** con DataStore: al reabrir la app la sesión sigue iniciada.
- **Verificación de identidad** después de ingresar:
  - Prueba de vida (recomendada): la cámara frontal y ML Kit piden 3 gestos al azar (parpadear, sonreír, girar o mover la cabeza). Una foto no puede hacerlos.
  - Verificación fácil: rostro, huella o PIN del teléfono (BiometricPrompt).
  - Huella digital (biometría fuerte).
  - Contraseña de la cuenta.
- **Inicio** con propósito, modalidades de colaboración e historia de impacto. Diseño adaptable con WindowSizeClass (una o dos columnas).
- **Catálogo de programas y productos** con filtro dinámico mientras se escribe y tarjetas que se expanden con el destino del aporte.
- **Formulario "Quiero colaborar"** con texto, número entero, fecha (DatePicker), lista desplegable, switch y checkbox, todos validados con mensaje e ícono de error.
- **CRUD de colaboraciones** en Room: crear, listar, editar y eliminar (con confirmación).
- **Perfil** con foto desde la galería o la cámara (FileProvider) mostrada en forma circular.
- **Notificación local** al registrar una colaboración.
- **Animaciones**: transiciones entre pantallas, mensajes de error y éxito animados, cambios de color, tarjetas expandibles e indicador de carga.
- **Modo oscuro** propio de Material 3 (fondo negro y violeta) y soporte de rotación de pantalla.

## Arquitectura (MVVM)

```
app/src/main/java/com/example/albalab/
├── MainActivity.kt          Punto de entrada
├── navigation/              Rutas (sealed class Screen), eventos y NavHost
├── viewmodel/               Estado de las pantallas y validaciones
├── data/
│   ├── model/               Entidades de Room y estados de formularios
│   ├── local/               Base de datos Room, DAOs y DataStore
│   └── repository/          Obtienen y guardan los datos
└── ui/
    ├── theme/               Colores, tipografía y tema
    ├── components/          Composables reutilizables
    ├── screens/             Pantallas
    └── utils/               Tamaño de pantalla y formato de fechas
```

## Tecnologías

Kotlin, Jetpack Compose, Material 3, Navigation Compose, ViewModel + StateFlow, Room, DataStore, Coil, CameraX, ML Kit Face Detection y Biometric.

## Cómo ejecutar

1. Clonar el repositorio y abrirlo en Android Studio.
2. Esperar a que Gradle sincronice las dependencias.
3. Crear un dispositivo en Device Manager (por ejemplo Pixel con API 35) o conectar un teléfono Android con depuración USB.
4. Presionar **Run**.
5. Crear una cuenta en "Regístrate" y elegir un método de verificación.

La prueba de vida necesita cámara frontal: se recomienda un teléfono real. En el emulador se puede usar la huella simulada (Controles extendidos > Fingerprint) o la contraseña de la cuenta.

## Requisitos

- Android Studio actualizado
- Android 8.0 (API 26) o superior
