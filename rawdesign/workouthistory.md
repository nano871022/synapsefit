<!DOCTYPE html>

<html class="dark" lang="es"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>Historial - Kinetic Pulse</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com" rel="preconnect"/>
<link crossorigin="" href="https://fonts.gstatic.com" rel="preconnect"/>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@700;800&amp;family=Inter:wght@400;600&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "secondary-fixed-dim": "#c6c6c9",
                        "on-surface": "#e0e3e6",
                        "tertiary-container": "#dddde4",
                        "tertiary-fixed-dim": "#c5c6cc",
                        "outline-variant": "#3a494a",
                        "on-background": "#e0e3e6",
                        "on-error": "#690005",
                        "on-tertiary": "#2e3035",
                        "surface": "#101416",
                        "on-primary": "#003739",
                        "surface-dim": "#101416",
                        "primary-fixed-dim": "#00dce5",
                        "error-container": "#93000a",
                        "on-primary-container": "#006c71",
                        "surface-bright": "#363a3c",
                        "secondary-container": "#454749",
                        "error": "#ffb4ab",
                        "inverse-on-surface": "#2d3133",
                        "surface-container-lowest": "#0b0f11",
                        "on-surface-variant": "#b9caca",
                        "surface-container": "#1c2023",
                        "background": "#101416",
                        "primary": "#e9feff",
                        "secondary-fixed": "#e2e2e5",
                        "surface-container-highest": "#323538",
                        "inverse-primary": "#00696e",
                        "on-tertiary-fixed-variant": "#45474c",
                        "tertiary-fixed": "#e2e2e8",
                        "outline": "#849495",
                        "primary-container": "#00f5ff",
                        "surface-variant": "#323538",
                        "on-secondary-container": "#b4b5b7",
                        "on-secondary": "#2f3133",
                        "surface-container-low": "#181c1e",
                        "on-error-container": "#ffdad6",
                        "primary-fixed": "#63f7ff",
                        "inverse-surface": "#e0e3e6",
                        "on-primary-fixed": "#002021",
                        "on-primary-fixed-variant": "#004f53",
                        "tertiary": "#faf9ff",
                        "on-tertiary-fixed": "#191c20",
                        "surface-container-high": "#272a2d",
                        "surface-tint": "#00dce5",
                        "on-tertiary-container": "#5f6167",
                        "on-secondary-fixed": "#1a1c1e",
                        "on-secondary-fixed-variant": "#454749",
                        "secondary": "#c6c6c9"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "md": "16px",
                        "gutter": "16px",
                        "xl": "32px",
                        "margin-edge": "16px",
                        "lg": "24px",
                        "xs": "4px",
                        "sm": "8px",
                        "unit": "4px"
                    },
                    "fontFamily": {
                        "body-md": ["Inter"],
                        "title-md": ["Inter"],
                        "headline-lg": ["Hanken Grotesk"],
                        "display-lg": ["Hanken Grotesk"],
                        "headline-lg-mobile": ["Hanken Grotesk"],
                        "label-sm": ["JetBrains Mono"]
                    },
                    "fontSize": {
                        "body-md": ["16px", { "lineHeight": "24px", "fontWeight": "400" }],
                        "title-md": ["18px", { "lineHeight": "24px", "fontWeight": "600" }],
                        "headline-lg": ["32px", { "lineHeight": "40px", "fontWeight": "700" }],
                        "display-lg": ["48px", { "lineHeight": "56px", "letterSpacing": "-0.02em", "fontWeight": "800" }],
                        "headline-lg-mobile": ["28px", { "lineHeight": "36px", "fontWeight": "700" }],
                        "label-sm": ["12px", { "lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "500" }]
                    }
                }
            }
        }
    </script>
<style>
        body { background-color: theme('colors.background'); color: theme('colors.on-background'); }
        .glass-card {
            background: linear-gradient(145deg, theme('colors.surface-container-low') 0%, theme('colors.surface-container-highest') 100%);
            border: 1px solid rgba(255, 255, 255, 0.05);
            box-shadow: 0 4px 30px rgba(0, 0, 0, 0.5);
            backdrop-filter: blur(10px);
        }
        .neon-text {
            color: theme('colors.primary-container');
            text-shadow: 0 0 10px rgba(0, 245, 255, 0.5);
        }
        .neon-border {
            border-color: theme('colors.primary-container');
            box-shadow: 0 0 10px rgba(0, 245, 255, 0.2);
        }
    </style>
<style>
    body {
      min-height: max(884px, 100dvh);
    }
  </style>
  </head>
<body class="antialiased min-h-screen pb-24 font-body-md text-body-md overflow-x-hidden">
<!-- TopAppBar -->
<header class="fixed top-0 w-full z-50 bg-surface border-b border-outline-variant flex items-center justify-between px-margin-edge h-16 max-w-screen-xl mx-auto">
<div class="flex items-center gap-sm cursor-pointer hover:bg-surface-container-highest transition-colors active:scale-95 duration-150 p-2 rounded-full">
<span class="material-symbols-outlined text-primary-container" data-icon="bolt">bolt</span>
</div>
<h1 class="font-display-lg text-display-lg font-black text-primary-container tracking-tighter">Historial</h1>
<div class="flex items-center gap-sm cursor-pointer hover:bg-surface-container-highest transition-colors active:scale-95 duration-150 p-2 rounded-full">
<div class="w-8 h-8 rounded-full bg-surface-container-highest overflow-hidden border border-outline-variant">
<img class="w-full h-full object-cover" data-alt="A futuristic, high-contrast avatar portrait of an athlete in a dimly lit, cyberpunk-inspired gym setting. The lighting is moody with subtle cyan and magenta rim lights highlighting their facial features. The overall tone is intense, modern, and energetic, perfectly fitting a high-tech fitness app's profile picture." src="https://lh3.googleusercontent.com/aida-public/AB6AXuC0jDwMw8xSQC6XRfS0Ned9otNtfHYyEllza8iihNqK1BOBuvFuP8wDyNf9e90yc7xVd6viF2efbBwcDu35mtPSJHfjSLlMIsB4AJuYzyi5zSaR1pyf3LVCoU8tjdaZdGdDBYvj-Dp-kEm43fIEUg1Qb9JgGYvdGbxGnXRFcwhjOklGKLeRtJBiA0mMMcBTuRJPFltQAwZK8BjKSKKn0kRiMFDM_GM_Pp5vM3d82n9taSpTb97gyNVo"/>
</div>
</div>
</header>
<main class="pt-24 px-margin-edge max-w-screen-md mx-auto flex flex-col gap-gutter">
<!-- Calendar Widget -->
<section class="glass-card rounded-xl p-md flex flex-col gap-sm">
<div class="flex justify-between items-center mb-sm">
<h2 class="font-title-md text-title-md text-on-surface">Octubre 2023</h2>
<div class="flex gap-2">
<button class="p-1 rounded bg-surface-container hover:bg-surface-container-highest transition-colors"><span class="material-symbols-outlined text-on-surface-variant text-sm">chevron_left</span></button>
<button class="p-1 rounded bg-surface-container hover:bg-surface-container-highest transition-colors"><span class="material-symbols-outlined text-on-surface-variant text-sm">chevron_right</span></button>
</div>
</div>
<div class="grid grid-cols-7 gap-xs text-center font-label-sm text-label-sm text-on-surface-variant">
<span>L</span><span>M</span><span>X</span><span>J</span><span>V</span><span>S</span><span>D</span>
<!-- Week 1 -->
<div class="py-1 rounded bg-surface-container-lowest text-on-surface-variant opacity-50">25</div>
<div class="py-1 rounded bg-surface-container-lowest text-on-surface-variant opacity-50">26</div>
<div class="py-1 rounded bg-surface-container-lowest text-on-surface-variant opacity-50">27</div>
<div class="py-1 rounded bg-surface-container-lowest text-on-surface-variant opacity-50">28</div>
<div class="py-1 rounded bg-surface-container-lowest text-on-surface-variant opacity-50">29</div>
<div class="py-1 rounded bg-surface-container-lowest text-on-surface-variant opacity-50">30</div>
<div class="py-1 rounded bg-surface-container">1</div>
<!-- Week 2 -->
<div class="py-1 rounded bg-surface-container">2</div>
<div class="py-1 rounded bg-surface-container border border-primary-container text-primary-container relative">
                    3
                    <span class="absolute bottom-0.5 left-1/2 transform -translate-x-1/2 w-1 h-1 bg-primary-container rounded-full"></span>
</div>
<div class="py-1 rounded bg-surface-container">4</div>
<div class="py-1 rounded bg-primary-container text-on-primary-container shadow-[0_0_10px_rgba(0,245,255,0.4)] font-bold">5</div>
<div class="py-1 rounded bg-surface-container">6</div>
<div class="py-1 rounded bg-surface-container border border-primary-container text-primary-container relative">
                    7
                    <span class="absolute bottom-0.5 left-1/2 transform -translate-x-1/2 w-1 h-1 bg-primary-container rounded-full"></span>
</div>
<div class="py-1 rounded bg-surface-container">8</div>
</div>
</section>
<!-- Summary Stats -->
<section class="grid grid-cols-2 gap-gutter">
<div class="glass-card rounded-lg p-md flex flex-col items-center justify-center text-center">
<span class="material-symbols-outlined text-primary-container mb-xs" style="font-variation-settings: 'FILL' 1;">fitness_center</span>
<span class="font-headline-lg-mobile text-headline-lg-mobile neon-text">4</span>
<span class="font-label-sm text-label-sm text-on-surface-variant mt-1 uppercase">Entrenos esta sem.</span>
</div>
<div class="glass-card rounded-lg p-md flex flex-col items-center justify-center text-center">
<span class="material-symbols-outlined text-primary-container mb-xs" style="font-variation-settings: 'FILL' 1;">timer</span>
<span class="font-headline-lg-mobile text-headline-lg-mobile neon-text">4.2h</span>
<span class="font-label-sm text-label-sm text-on-surface-variant mt-1 uppercase">Tiempo Total</span>
</div>
</section>
<!-- Training List -->
<section class="flex flex-col gap-sm mt-md">
<h3 class="font-title-md text-title-md text-on-surface mb-xs">Entrenamientos Registrados</h3>
<!-- Session Card 1 -->
<div class="glass-card rounded-xl p-md border-l-4 border-l-primary-container hover:bg-surface-container-highest transition-colors cursor-pointer group">
<div class="flex justify-between items-start mb-md">
<div>
<h4 class="font-title-md text-title-md text-on-surface group-hover:text-primary-container transition-colors">Día de Empuje (Push)</h4>
<p class="font-label-sm text-label-sm text-on-surface-variant mt-xs">Jueves, 5 Oct • 18:30</p>
</div>
<div class="bg-surface-container-lowest px-2 py-1 rounded text-primary-container font-label-sm text-label-sm flex items-center gap-1">
<span class="material-symbols-outlined text-[14px]">timer</span> 65 min
                    </div>
</div>
<div class="flex flex-wrap gap-2 mb-md">
<span class="px-2 py-1 bg-surface-container rounded-full font-label-sm text-label-sm text-on-surface border border-outline-variant">Pecho</span>
<span class="px-2 py-1 bg-surface-container rounded-full font-label-sm text-label-sm text-on-surface border border-outline-variant">Hombros</span>
<span class="px-2 py-1 bg-surface-container rounded-full font-label-sm text-label-sm text-on-surface border border-outline-variant">Tríceps</span>
</div>
<div class="flex justify-between items-center pt-sm border-t border-outline-variant">
<div class="flex flex-col">
<span class="font-label-sm text-label-sm text-on-surface-variant uppercase">Volumen Total</span>
<span class="font-body-md text-body-md text-on-surface font-semibold">12,450 kg</span>
</div>
<div class="flex flex-col text-right">
<span class="font-label-sm text-label-sm text-on-surface-variant uppercase">Ejercicios</span>
<span class="font-body-md text-body-md text-on-surface font-semibold">6</span>
</div>
</div>
</div>
<!-- Session Card 2 -->
<div class="glass-card rounded-xl p-md border-l-4 border-l-surface-variant hover:border-l-primary-container hover:bg-surface-container-highest transition-all cursor-pointer group opacity-90">
<div class="flex justify-between items-start mb-md">
<div>
<h4 class="font-title-md text-title-md text-on-surface group-hover:text-primary-container transition-colors">Cuerpo Completo</h4>
<p class="font-label-sm text-label-sm text-on-surface-variant mt-xs">Martes, 3 Oct • 07:15</p>
</div>
<div class="bg-surface-container-lowest px-2 py-1 rounded text-on-surface-variant font-label-sm text-label-sm flex items-center gap-1">
<span class="material-symbols-outlined text-[14px]">timer</span> 75 min
                    </div>
</div>
<div class="flex flex-wrap gap-2 mb-md">
<span class="px-2 py-1 bg-surface-container rounded-full font-label-sm text-label-sm text-on-surface-variant border border-outline-variant">Piernas</span>
<span class="px-2 py-1 bg-surface-container rounded-full font-label-sm text-label-sm text-on-surface-variant border border-outline-variant">Espalda</span>
<span class="px-2 py-1 bg-surface-container rounded-full font-label-sm text-label-sm text-on-surface-variant border border-outline-variant">Core</span>
</div>
<div class="flex justify-between items-center pt-sm border-t border-outline-variant">
<div class="flex flex-col">
<span class="font-label-sm text-label-sm text-on-surface-variant uppercase">Volumen Total</span>
<span class="font-body-md text-body-md text-on-surface font-semibold">18,200 kg</span>
</div>
<div class="flex flex-col text-right">
<span class="font-label-sm text-label-sm text-on-surface-variant uppercase">Ejercicios</span>
<span class="font-body-md text-body-md text-on-surface font-semibold">8</span>
</div>
</div>
</div>
<button class="mt-md py-3 w-full rounded-lg border border-primary-container text-primary-container font-label-sm text-label-sm uppercase hover:bg-primary-container hover:text-on-primary-container transition-colors neon-border">
                Cargar Más Historial
            </button>
</section>
</main>
<!-- BottomNavBar -->
<nav class="fixed bottom-0 left-0 w-full flex justify-around items-center h-20 px-4 pb-safe bg-surface-container z-50 rounded-t-xl border-t border-outline-variant shadow-lg md:hidden">
<div class="flex flex-col items-center justify-center text-on-surface-variant px-4 py-1 hover:text-primary-fixed-dim active:scale-90 transition-transform duration-200 cursor-pointer">
<span class="material-symbols-outlined" data-icon="fitness_center">fitness_center</span>
<span class="font-label-sm text-label-sm mt-1">Workouts</span>
</div>
<div class="flex flex-col items-center justify-center bg-primary-container text-on-primary-container rounded-full px-4 py-1 active:scale-90 transition-transform duration-200 cursor-pointer">
<span class="material-symbols-outlined" data-icon="history" data-weight="fill" style="font-variation-settings: 'FILL' 1;">history</span>
<span class="font-label-sm text-label-sm mt-1">History</span>
</div>
<div class="flex flex-col items-center justify-center text-on-surface-variant px-4 py-1 hover:text-primary-fixed-dim active:scale-90 transition-transform duration-200 cursor-pointer">
<span class="material-symbols-outlined" data-icon="insights">insights</span>
<span class="font-label-sm text-label-sm mt-1">Metrics</span>
</div>
<div class="flex flex-col items-center justify-center text-on-surface-variant px-4 py-1 hover:text-primary-fixed-dim active:scale-90 transition-transform duration-200 cursor-pointer">
<span class="material-symbols-outlined" data-icon="person">person</span>
<span class="font-label-sm text-label-sm mt-1">Profile</span>
</div>
</nav>
</body></html>
