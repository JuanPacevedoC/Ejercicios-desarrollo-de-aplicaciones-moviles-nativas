# 🏋️‍♂️ FitMarket Calisthenics — Tienda Deportiva Virtual

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Material Components](https://img.shields.io/badge/UI-Material%20Design%203-blue?style=for-the-badge&logo=materialdesign&logoColor=white)](https://material.io/develop/android)
[![MinSdk](https://img.shields.io/badge/MinSdk-24%20(Nougat)-orange?style=for-the-badge)](https://developer.android.com/)
[![TargetSdk](https://img.shields.io/badge/TargetSdk-36-brightgreen?style=for-the-badge)](https://developer.android.com/)

**FitMarket Calisthenics** es un aplicativo móvil nativo para Android diseñado y desarrollado como propuesta de solución integral de comercio electrónico para equipamiento, barras, anillas y accesorios de calistenia y entrenamiento con peso corporal.

El desarrollo corresponde a la **Actividad 2 / Fase de Frontend** del programa de **Ingeniería de Software de la Fundación Universitaria Compensar**, integrando interfaces de usuario nativas (XML, Layouts, Drawables, Styles), arquitectura basada en Fragments y ViewBinding, navegación dinámica multicapa (Drawer y Bottom Navigation), autenticación biométrica y riguroso cumplimiento de la legislación colombiana sobre comercio electrónico y protección al consumidor.

---

## 📌 Tabla de Contenidos

- [Información Académica](#-información-académica)
- [Arquitectura del Sistema](#-arquitectura-del-sistema)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Módulos e Interfaces por Rol](#-módulos-e-interfaces-por-rol)
  - [1. Módulo Transversal](#1-módulo-transversal-todos-los-roles)
  - [2. Rol Comprador (Cliente)](#2-rol-comprador-cliente)
  - [3. Rol Vendedor (Aliado Comercial)](#3-rol-vendedor-aliado-comercial)
  - [4. Rol Administrador](#4-rol-administrador)
- [Cumplimiento Legal y Normativo (Colombia)](#-cumplimiento-legal-y-normativo-colombia)
- [Diseño y Recursos Gráficos](#-diseño-y-recursos-gráficos-res)
- [Pila Tecnológica y Dependencias](#-pila-tecnológica-y-dependencias)
- [Instalación, Compilación y Pruebas](#-instalación-compilación-y-pruebas)
- [Credenciales de Prueba y Demostración](#-credenciales-de-prueba-y-demostración)
- [Recursos de Sustentación](#-recursos-de-sustentación)

---

## 🎓 Información Académica

* **Institución:** Fundación Universitaria Compensar  
* **Facultad:** Ingeniería, Diseño e Innovación  
* **Programa:** Ingeniería de Software  
* **Espacio Académico:** Ejercicios de Profundización / Aplicaciones Móviles  
* **Estudiante:** Juan Pablo Acevedo Cabrera  
* **Entregable:** Desarrollo de Interfaces Frontend y Sustentación Técnica  

---

## 🏛 Arquitectura del Sistema

El proyecto sigue una arquitectura **Android Nativa desacoplada** con un flujo unidireccional de datos en la capa de presentación:

```
                               ┌────────────────────────────────┐
                               │         SplashActivity         │
                               └───────────────┬────────────────┘
                                               │
                               ┌───────────────▼────────────────┐
                               │         LoginActivity          │
                               │  (Biometría + Roles Selector)  │
                               └───────────────┬────────────────┘
                                               │
                               ┌───────────────▼────────────────┐
                               │          MainActivity          │
                               │  (DrawerLayout + BottomNavHost)│
                               └───────┬────────────────┬───────┘
                                       │                │
            ┌──────────────────────────┼──────────────────────────┐
            │                          │                          │
  ┌─────────▼─────────┐      ┌─────────▼─────────┐      ┌─────────▼─────────┐
  │   Rol Comprador   │      │   Rol Vendedor    │      │ Rol Administrador │
  ├───────────────────┤      ├───────────────────┤      ├───────────────────┤
  │ • BuyerHome       │      │ • VendorProducts  │      │ • AdminDashboard  │
  │ • BuyerCatalog    │      │ • VendorOrders    │      │ • AdminUsers      │
  │ • BuyerDetail     │      │ • VendorProfile   │      │ • AdminProducts   │
  │ • BuyerCart       │      └───────────────────┘      │ • AdminReports    │
  │ • BuyerCheckout   │                                 └───────────────────┘
  │ • BuyerProfile    │
  └───────────────────┘
            │                          │                          │
            └──────────────────────────┼──────────────────────────┘
                                       │
                        ┌──────────────▼─────────────┐
                        │    AppDataRepository       │
                        │ (Singleton In-Memory State)│
                        └────────────────────────────┘
```

### Características Principales de la Arquitectura:
1. **Single Activity Pattern Host (`MainActivity`)**: Administra un `FragmentContainerView` principal junto a un `DrawerLayout` y un `BottomNavigationView` sincronizados.
2. **Re-inflado Dinámico de Menús**: Al alternar de rol (mediante chip o menú), la actividad reemplaza en tiempo de ejecución los archivos de menú XML (`res/menu/`) para aislar los privilegios visuales.
3. **ViewBinding Habilitado**: Eliminación completa de `findViewById`, garantizando seguridad de tipos en tiempo de compilación y mayor rendimiento.
4. **Data Repository Centralizado (`AppDataRepository`)**: Modelo en memoria desacoplado con soporte para ciclo de vida de carritos, cálculo financiero con moneda nacional en COP, decremento automático de stock por orden y auditoría de eventos.

---

## 📂 Estructura del Proyecto

```
Ejerciciosprofundizacin/
├── app/
│   ├── build.gradle.kts                     # Configuración Gradle del módulo app y dependencias
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml          # Registro de Activities y permisos (USE_BIOMETRIC)
│   │   │   ├── java/com/example/ejerciciosprofundizacin/
│   │   │   │   ├── SplashActivity.kt        # Pantalla de bienvenida con animación y delay
│   │   │   │   ├── LoginActivity.kt         # Login con BiometricPrompt y fallback
│   │   │   │   ├── ForgotPasswordActivity.kt# Recuperación de contraseña y validación de correo
│   │   │   │   ├── MainActivity.kt          # Host principal: Drawer, BottomNav, badge y roles
│   │   │   │   ├── model/                   # Entidades de dominio
│   │   │   │   │   ├── UserRole.kt          # Enum de roles (BUYER, VENDOR, ADMIN)
│   │   │   │   │   ├── User.kt              # Datos de usuario y estado activo/inactivo
│   │   │   │   │   ├── Product.kt           # Ficha técnica de calistenia, stock y precios
│   │   │   │   │   ├── CartItem.kt          # Elemento del carrito de compras y subtotales
│   │   │   │   │   └── Order.kt             # Orden emitida, estado logístico y radicado PQR
│   │   │   │   ├── data/
│   │   │   │   │   └── AppDataRepository.kt # Repositorio en memoria y lógica de negocio
│   │   │   │   └── ui/
│   │   │   │       ├── adapters/            # Adaptadores de RecyclerView optimizados
│   │   │   │       │   ├── AdminUsersAdapter.kt
│   │   │   │       │   ├── AdminProductsAdapter.kt
│   │   │   │       │   ├── VendorProductsAdapter.kt
│   │   │   │       │   ├── VendorOrdersAdapter.kt
│   │   │   │       │   ├── BuyerProductAdapter.kt
│   │   │   │       │   └── BuyerCartAdapter.kt
│   │   │   │       ├── admin/               # Fragments del rol Administrador
│   │   │   │       │   ├── AdminDashboardFragment.kt
│   │   │   │       │   ├── AdminUsersFragment.kt
│   │   │   │       │   ├── AdminProductsFragment.kt
│   │   │   │       │   └── AdminReportsFragment.kt
│   │   │   │       ├── vendor/              # Fragments del rol Vendedor
│   │   │   │       │   ├── VendorProductsFragment.kt
│   │   │   │       │   ├── VendorOrdersFragment.kt
│   │   │   │       │   └── VendorProfileFragment.kt
│   │   │   │       └── buyer/               # Fragments del rol Comprador
│   │   │   │           ├── BuyerHomeFragment.kt
│   │   │   │           ├── BuyerCatalogFragment.kt
│   │   │   │           ├── BuyerProductDetailFragment.kt
│   │   │   │           ├── BuyerCartFragment.kt
│   │   │   │           ├── BuyerCheckoutFragment.kt
│   │   │   │           └── BuyerProfileFragment.kt
│   │   │   └── res/
│   │   │       ├── drawable/                # Iconos VectorDrawable XML y formas <shape>
│   │   │       ├── layout/                  # Layouts XML de Activities, Fragments e Items
│   │   │       ├── menu/                    # Menús XML de BottomNav y Drawer por rol
│   │   │       └── values/                  # colors.xml, strings.xml, themes.xml
│   │   └── test/java/.../ExampleUnitTest.kt # Pruebas unitarias de repositorio y carrito
├── gradle/libs.versions.toml                # Catálogo de dependencias versionadas
├── GUION_VIDEO_SUSTENTACION.txt             # Guion técnico cronometrado para grabación (5 min)
└── README.md                                # Documentación general del repositorio
```

---

## 📱 Módulos e Interfaces por Rol

### 1. Módulo Transversal (Todos los Roles)
* **Splash Screen (`SplashActivity.kt` / `activity_splash.xml`):** Pantalla de apertura con logotipo vectorial deportivo, eslogan institucional y animación de entrada.
* **Autenticación Biométrica (`LoginActivity.kt` / `activity_login.xml`):** Integración con la librería oficial `androidx.biometric.BiometricPrompt`. Valida huella dactilar/credenciales del sistema operativo e incluye diálogo de fallback para entornos de prueba o emuladores sin sensores enrolados. Incluye selector rápido de perfiles mediante `ChipGroup`.
* **Recuperación de Contraseña (`ForgotPasswordActivity.kt` / `activity_forgot_password.xml`):** Formulario con validación de expresiones regulares de correo electrónico y retroalimentación de token cifrado.
* **Navegación Unificada (`MainActivity.kt` / `activity_main.xml`):** Barra superior `MaterialToolbar` con indicador dinámico de rol activo, menú lateral deslizable (`NavigationDrawer`) y barra inferior (`BottomNavigationView`) con contador badge en tiempo real de artículos en el carrito.

---

### 2. Rol Comprador (Cliente)
* **Inicio (`BuyerHomeFragment.kt`):** Banner motivacional de calistenia, selector horizontal de categorías (`ChipGroup`) y carrusel de productos recomendados.
* **Catálogo Deportivo (`BuyerCatalogFragment.kt`):** Búsqueda reactiva por texto (nombre, material o categoría) y filtros por chips.
* **Detalle del Producto (`BuyerProductDetailFragment.kt`):** Ficha técnica especializada (capacidad de carga en kg, materiales de acero/abedul, dimensiones), selector de unidades, información de garantía legal (12 meses) y botones de acción rápida (*Añadir al Carrito* y *Comprar Ahora*).
* **Carrito de Compras (`BuyerCartFragment.kt`):** Modificación interactiva de cantidades (+/-), eliminación de artículos y desglose en tiempo real de: Subtotal, Costo de Envío e IVA (19% incluido en COP).
* **Pasarela de Pagos / Checkout (`BuyerCheckoutFragment.kt`):** Dirección de despacho, métodos de pago colombianos (**PSE**, Tarjetas de Crédito/Débito, **Nequi / Daviplata**) y confirmación de compra con radicado PQR oficial.
* **Perfil de Usuario (`BuyerProfileFragment.kt`):** Actualización de datos personales, consulta del historial de compras y políticas de protección al consumidor (Habeas Data).

---

### 3. Rol Vendedor (Aliado Comercial)
* **Panel de Catálogo (`VendorProductsFragment.kt`):** Inventario propio del comercio con listado de artículos, existencias en bodega, precio unitario y diálogo modal para registrar o modificar productos.
* **Historial de Pedidos Recibidos (`VendorOrdersFragment.kt`):** Vista cronológica de órdenes de compra recibidas con avance interactivo de estados logísticos:
  $$\text{Pendiente} \longrightarrow \text{En preparación} \longrightarrow \text{Enviado} \longrightarrow \text{Entregado}$$
* **Perfil de Vendedor (`VendorProfileFragment.kt`):** Razón social de la tienda, NIT verificado, bodega de despacho en Bogotá y Acuerdos de Nivel de Servicio (SLA de 24 horas hábiles).

---

### 4. Rol Administrador
* **Dashboard Analítico (`AdminDashboardFragment.kt`):** Tarjetas de métricas (KPIs) en tiempo real con valor total facturado en COP, inventario valorizado, usuarios activos y bitácora de auditoría reciente.
* **Gestión de Usuarios - CRUD (`AdminUsersFragment.kt`):** Listado paginado con filtros por rol, buscador por nombre/correo, modal para crear usuarios, conmutador (*Switch*) para habilitar/deshabilitar cuentas y eliminación segura con confirmación.
* **Moderación de Productos (`AdminProductsFragment.kt`):** Control global del catálogo, revisión de especificaciones y actualización de precios/stock.
* **Reportes de Ventas (`AdminReportsFragment.kt`):** Desglose financiero por categorías de calistenia, balance de órdenes y simulación de exportación contable para DIAN.

---

## ⚖️ Cumplimiento Legal y Normativo (Colombia)

El proyecto da respuesta directa a los requerimientos de completitud de la **Actividad 1**, incorporando los principios del marco jurídico colombiano en las pantallas correspondientes:

| Ley / Norma | Denominación | Aplicación en la App | Pantalla / Código |
| :--- | :--- | :--- | :--- |
| **Ley 1581 de 2012** | Protección de Datos Personales (Habeas Data) | Consentimiento informado de tratamiento de datos, opción de consulta, actualización y supresión de datos personales. | [`BuyerProfileFragment`](file:///C:/Users/juanp/AndroidStudioProjects/Ejerciciosprofundizacin/app/src/main/java/com/example/ejerciciosprofundizacin/ui/buyer/BuyerProfileFragment.kt) |
| **Ley 1480 de 2011** | Estatuto del Consumidor | Información pública de garantía mínima legal (12 meses), advertencia del derecho de retracto (5 días hábiles) y reversión de pago. | [`BuyerProductDetailFragment`](file:///C:/Users/juanp/AndroidStudioProjects/Ejerciciosprofundizacin/app/src/main/java/com/example/ejerciciosprofundizacin/ui/buyer/BuyerProductDetailFragment.kt) |
| **Ley 527 de 1999** | Mensajes de Datos y Comercio Electrónico | Generación de comprobante digital cifrado e inalterable tras la confirmación de la transacción en la pasarela. | [`BuyerCheckoutFragment`](file:///C:/Users/juanp/AndroidStudioProjects/Ejerciciosprofundizacin/app/src/main/java/com/example/ejerciciosprofundizacin/ui/buyer/BuyerCheckoutFragment.kt) |
| **Circular 006 de 2014 SIC** | Trazabilidad de Quejas y Reclamos | Asignación automática de número de radicado PQR en cada orden (ej. `PQR-2026-0044`) para seguimiento ante la SIC. | [`BuyerCheckoutFragment`](file:///C:/Users/juanp/AndroidStudioProjects/Ejerciciosprofundizacin/app/src/main/java/com/example/ejerciciosprofundizacin/ui/buyer/BuyerCheckoutFragment.kt) |

---

## 🎨 Diseño y Recursos Gráficos (`res`)

### 1. Paleta de Colores (`res/values/colors.xml`)
* **Primario:** `#F4511E` (Naranja atlético intenso de calistenia y energía).
* **Secundario:** `#00897B` (Verde azulado para acentos de éxito y compra).
* **Superficies:** `#121212` / `#1E1E1E` (Tema oscuro de alto contraste y legibilidad).
* **Badges Semánticos:**
  * Comprador: `#1976D2` (Azul).
  * Vendedor: `#388E3C` (Verde).
  * Administrador: `#7B1FA2` (Púrpura).

### 2. Recursos Vectoriales (`res/drawable/`)
* Uso exclusivo de `VectorDrawable` XML para todos los íconos (`ic_logo.xml`, `ic_fingerprint.xml`, `ic_fitness_center.xml`, etc.), garantizando renderizado perfecto en cualquier densidad (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`) sin pérdida de nitidez y reduciendo el tamaño del APK.
* Selectores de forma `<shape>` con esquinas redondeadas (`bg_badge_orange.xml`, `bg_card_surface.xml`) procesados nativamente por hardware.

### 3. Estilos y Temas (`res/values/themes.xml`)
* Herencia de `Theme.MaterialComponents.DayNight.NoActionBar` para soporte nativo de modo claro/oscuro y uso de `MaterialToolbar` personalizada.

---

## 🛠 Pila Tecnológica y Dependencias

* **Lenguaje:** Kotlin 2.0+
* **Plataforma:** Android SDK (minSdk 24, targetSdk 36)
* **Herramienta de Construcción:** Gradle 8.9 con Android Gradle Plugin (AGP) 8.7.3
* **Librerías Clave:**
  * `androidx.core:core-ktx:1.15.0`
  * `androidx.appcompat:appcompat:1.7.0`
  * `com.google.android.material:material:1.12.0`
  * `androidx.biometric:biometric:1.2.0-alpha05` (Autenticación Biométrica)
  * `androidx.fragment:fragment-ktx:1.8.5` (Gestión avanzada de Fragments)
  * `androidx.recyclerview:recyclerview:1.3.2` (Listados optimizados)
  * `androidx.cardview:cardview:1.0.0` (Tarjetas de contenido elevado)

---

## 🚀 Instalación, Compilación y Pruebas

### Prerrequisitos:
1. Android Studio Ladybug (2024.2) o superior.
2. JDK 17 o 21 configurado en Android Studio.
3. Dispositivo físico Android (API 24+) o Emulador AVD (ej. Pixel 7 / Android 14+).

### Clonar y Compilar:
```bash
# Clonar el repositorio
git clone <URL_DEL_REPOSITORIO>

# Ingresar a la carpeta del proyecto
cd Ejerciciosprofundizacin

# Compilar la aplicación en modo Debug
./gradlew assembleDebug

# Ejecutar las pruebas unitarias
./gradlew testDebugUnitTest
```

---

## 🔑 Credenciales de Prueba y Demostración

La aplicación cuenta con una barra de acceso directo para cambiar de rol en cualquier momento, o puedes utilizar los siguientes usuarios precargados en la pantalla de Login:

| Rol | Correo Electrónico | Contraseña | Perfil Demostrativo |
| :--- | :--- | :--- | :--- |
| **Comprador** | `jpabloacevedo@ucompensar.edu.co` | `calistenia2026` | Juan Pablo Acevedo (Atleta de Calistenia) |
| **Vendedor** | `ventas@irongrip.co` | `irongrip123` | IronGrip Calisthenics (Tienda Aliada) |
| **Administrador** | `admin@fitmarket.com` | `adminPass2026` | Super Administrador FitMarket |

> **Nota sobre Biometría:** En dispositivos o emuladores sin huella enrolada, el sistema activa automáticamente un cuadro de diálogo nativo de confirmación para permitir la validación del flujo biométrico sin bloqueos de hardware.

---

*Desarrollado por **Juan Pablo Acevedo Cabrera** — Fundación Universitaria Compensar (2026).*
