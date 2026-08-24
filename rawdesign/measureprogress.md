<!DOCTYPE html>

<html class="dark" lang="en"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>Progreso - Kinetic Pulse</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@700;800&amp;family=Inter:wght@400;600&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "inverse-surface": "#e0e3e6",
                        "background": "#101416",
                        "on-tertiary": "#2e3035",
                        "surface-container-high": "#272a2d",
                        "on-secondary": "#2f3133",
                        "on-primary-fixed": "#002021",
                        "outline": "#849495",
                        "primary-fixed": "#63f7ff",
                        "on-surface-variant": "#b9caca",
                        "error": "#ffb4ab",
                        "tertiary": "#faf9ff",
                        "primary": "#e9feff",
                        "primary-fixed-dim": "#00dce5",
                        "on-surface": "#e0e3e6",
                        "surface-container": "#1c2023",
                        "on-error": "#690005",
                        "on-primary": "#003739",
                        "surface-tint": "#00dce5",
                        "tertiary-fixed": "#e2e2e8",
                        "on-error-container": "#ffdad6",
                        "on-primary-container": "#006c71",
                        "primary-container": "#00f5ff",
                        "on-tertiary-fixed-variant": "#45474c",
                        "secondary-fixed": "#e2e2e5",
                        "outline-variant": "#3a494a",
                        "on-secondary-fixed-variant": "#454749",
                        "surface-container-highest": "#323538",
                        "on-primary-fixed-variant": "#004f53",
                        "error-container": "#93000a",
                        "inverse-primary": "#00696e",
                        "tertiary-container": "#dddde4",
                        "tertiary-fixed-dim": "#c5c6cc",
                        "on-background": "#e0e3e6",
                        "secondary": "#c6c6c9",
                        "surface-bright": "#363a3c",
                        "surface": "#101416",
                        "secondary-fixed-dim": "#c6c6c9",
                        "on-tertiary-container": "#5f6167",
                        "on-tertiary-fixed": "#191c20",
                        "on-secondary-fixed": "#1a1c1e",
                        "secondary-container": "#454749",
                        "surface-dim": "#101416",
                        "surface-variant": "#323538",
                        "inverse-on-surface": "#2d3133",
                        "surface-container-lowest": "#0b0f11",
                        "surface-container-low": "#181c1e",
                        "on-secondary-container": "#b4b5b7"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "xs": "4px",
                        "md": "16px",
                        "margin-edge": "16px",
                        "unit": "4px",
                        "gutter": "16px",
                        "sm": "8px",
                        "lg": "24px",
                        "xl": "32px"
                    },
                    "fontFamily": {
                        "headline-lg-mobile": ["Hanken Grotesk"],
                        "display-lg": ["Hanken Grotesk"],
                        "label-sm": ["JetBrains Mono"],
                        "headline-lg": ["Hanken Grotesk"],
                        "title-md": ["Inter"],
                        "body-md": ["Inter"]
                    },
                    "fontSize": {
                        "headline-lg-mobile": ["28px", { "lineHeight": "36px", "fontWeight": "700" }],
                        "display-lg": ["48px", { "lineHeight": "56px", "letterSpacing": "-0.02em", "fontWeight": "800" }],
                        "label-sm": ["12px", { "lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "500" }],
                        "headline-lg": ["32px", { "lineHeight": "40px", "fontWeight": "700" }],
                        "title-md": ["18px", { "lineHeight": "24px", "fontWeight": "600" }],
                        "body-md": ["16px", { "lineHeight": "24px", "fontWeight": "400" }]
                    }
                }
            }
        }
    </script>
<style>
        .neon-graph-line {
            filter: drop-shadow(0 0 8px rgba(0, 245, 255, 0.6));
        }
        .neon-dot {
            box-shadow: 0 0 10px rgba(0, 245, 255, 0.8);
        }
        
        /* Hide scrollbar for horizontal scrolling zones */
        .no-scrollbar::-webkit-scrollbar {
            display: none;
        }
        .no-scrollbar {
            -ms-overflow-style: none;
            scrollbar-width: none;
        }
    </style>
<style>
    body {
      min-height: max(884px, 100dvh);
    }
  </style>
  </head>
<body class="bg-background text-on-background font-body-md min-h-screen flex flex-col pb-24">
<!-- TopAppBar -->
<header class="w-full top-0 sticky z-40 bg-background dark:bg-background flat no shadows">
<div class="flex items-center justify-between px-margin-edge py-md w-full">
<div class="flex items-center gap-3">
<div class="w-10 h-10 rounded-full overflow-hidden bg-surface-container border border-outline-variant">
<img alt="User profile picture" class="w-full h-full object-cover" data-alt="A close up, highly detailed portrait of an athletic young adult with a focused expression, wearing sleek futuristic dark gym wear with subtle neon accents. The lighting is moody and cinematic, consistent with a high-tech fitness application's dark mode aesthetic. The background is slightly blurred showing gym equipment." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCPc6E5j6HZgbTrBHTrsZAGZbv7-cU4_6IXt40q_9hbpKuvkJzqrPKQ_L2fS6_WxDgocc12WkSkk0yFzbjcQ0iW-im_9psO1uegeCa1fm6q90NmKYigIGzrevI7qbaAaMiJHOOKlwvUfJAFeXiGlEf1wtqSkRBZ9_1-ESeTJrQRePqunZAzYnyq7t4T3wICcpncp43z3Duidda1yo4NDysy7l7negSXePV5qL9ax_KvLuM4Nn8fAIgB"/>
</div>
<h1 class="font-headline-lg-mobile text-headline-lg-mobile font-bold text-primary dark:text-primary tracking-tight">Kinetic Pulse</h1>
</div>
<button class="text-on-surface-variant dark:text-on-surface-variant hover:bg-surface-container-high dark:hover:bg-surface-container-high transition-colors active:scale-95 p-2 rounded-full flex items-center justify-center">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">notifications</span>
</button>
</div>
<div class="px-margin-edge pb-sm">
<h2 class="font-title-md text-title-md text-on-surface">Progreso</h2>
</div>
</header>
<!-- Main Content Canvas -->
<main class="flex-1 px-margin-edge flex flex-col gap-gutter mt-sm">
<!-- Time Range Selector -->
<section class="bg-surface-container rounded-lg p-1 border border-outline-variant/30 flex justify-between items-center w-full">
<button class="flex-1 py-1 px-2 rounded-md font-label-sm text-label-sm text-on-surface-variant hover:text-primary transition-colors">1M</button>
<button class="flex-1 py-1 px-2 rounded-md font-label-sm text-label-sm text-on-surface-variant hover:text-primary transition-colors">3M</button>
<button class="flex-1 py-1 px-2 rounded-md font-label-sm text-label-sm bg-primary-container text-on-primary-container shadow-[0_0_10px_rgba(0,245,255,0.3)] transition-colors">6M</button>
<button class="flex-1 py-1 px-2 rounded-md font-label-sm text-label-sm text-on-surface-variant hover:text-primary transition-colors">1A</button>
<button class="flex-1 py-1 px-2 rounded-md font-label-sm text-label-sm text-on-surface-variant hover:text-primary transition-colors">Todo</button>
</section>
<!-- Main Graph: Peso -->
<section class="bg-surface-container rounded-xl p-md border border-outline-variant/20 relative overflow-hidden group">
<div class="absolute top-0 right-0 p-3 opacity-20 group-hover:opacity-100 transition-opacity">
<span class="material-symbols-outlined text-primary-container">monitoring</span>
</div>
<div class="mb-4">
<h3 class="font-label-sm text-label-sm text-on-surface-variant uppercase tracking-widest">Peso</h3>
<div class="flex items-baseline gap-2 mt-1">
<span class="font-headline-lg-mobile text-headline-lg-mobile text-primary">74.2</span>
<span class="font-title-md text-title-md text-on-surface-variant">kg</span>
<span class="ml-2 font-label-sm text-label-sm text-primary-container bg-primary-container/10 px-2 py-0.5 rounded flex items-center gap-1">
<span class="material-symbols-outlined text-[14px]">arrow_downward</span> -2.4kg
                    </span>
</div>
</div>
<!-- Graph Area (Simulated with SVG) -->
<div class="h-48 w-full mt-4 relative">
<!-- Grid Lines -->
<div class="absolute inset-0 flex flex-col justify-between border-l border-b border-outline-variant/30 pb-4 pl-4">
<div class="w-full border-t border-outline-variant/10 flex-1"></div>
<div class="w-full border-t border-outline-variant/10 flex-1"></div>
<div class="w-full border-t border-outline-variant/10 flex-1"></div>
<div class="w-full border-t border-outline-variant/10 flex-1"></div>
</div>
<!-- Y-Axis Labels -->
<div class="absolute left-0 top-0 h-full flex flex-col justify-between text-[10px] text-on-surface-variant/60 pb-4 font-label-sm">
<span>78</span>
<span>76</span>
<span>74</span>
<span>72</span>
</div>
<!-- X-Axis Labels -->
<div class="absolute bottom-0 left-4 w-[calc(100%-16px)] flex justify-between text-[10px] text-on-surface-variant/60 pt-1 font-label-sm">
<span>Ene</span>
<span>Feb</span>
<span>Mar</span>
<span>Abr</span>
<span>May</span>
<span>Jun</span>
</div>
<!-- Line & Area -->
<div class="absolute inset-0 ml-4 mb-4">
<svg class="w-full h-full overflow-visible" preserveaspectratio="none" viewbox="0 0 100 100">
<defs>
<lineargradient id="weightGrad" x1="0" x2="0" y1="0" y2="1">
<stop offset="0%" stop-color="#00f5ff" stop-opacity="0.3"></stop>
<stop offset="100%" stop-color="#00f5ff" stop-opacity="0"></stop>
</lineargradient>
</defs>
<!-- Area -->
<polygon fill="url(#weightGrad)" points="0,20 20,15 40,35 60,45 80,40 100,70 100,100 0,100"></polygon>
<!-- Line -->
<polyline class="neon-graph-line" fill="none" points="0,20 20,15 40,35 60,45 80,40 100,70" stroke="#00f5ff" stroke-linejoin="round" stroke-width="2"></polyline>
<!-- Data Points -->
<circle cx="0" cy="20" fill="#101416" r="2" stroke="#00f5ff" stroke-width="1.5"></circle>
<circle cx="20" cy="15" fill="#101416" r="2" stroke="#00f5ff" stroke-width="1.5"></circle>
<circle cx="40" cy="35" fill="#101416" r="2" stroke="#00f5ff" stroke-width="1.5"></circle>
<circle cx="60" cy="45" fill="#101416" r="2" stroke="#00f5ff" stroke-width="1.5"></circle>
<circle cx="80" cy="40" fill="#101416" r="2" stroke="#00f5ff" stroke-width="1.5"></circle>
<circle class="neon-dot" cx="100" cy="70" fill="#00f5ff" r="3"></circle>
</svg>
<!-- Tooltip (Simulated on last point) -->
<div class="absolute right-0 bottom-[30%] transform translate-x-2 -translate-y-8 bg-surface-container-high px-2 py-1 rounded border border-outline-variant/50 shadow-lg z-10">
<span class="font-label-sm text-label-sm text-primary">74.2</span>
</div>
</div>
</div>
</section>
<!-- Body Zone Selector -->
<section class="w-full">
<h3 class="font-label-sm text-label-sm text-on-surface-variant mb-2 ml-1">MÉTRICAS CORPORALES</h3>
<div class="flex overflow-x-auto gap-2 pb-2 no-scrollbar px-1">
<button class="shrink-0 px-4 py-2 rounded-full border border-primary-container bg-primary-container/10 text-primary-container font-label-sm text-label-sm transition-colors whitespace-nowrap">
                    Cintura
                </button>
<button class="shrink-0 px-4 py-2 rounded-full border border-outline-variant bg-surface text-on-surface-variant hover:border-primary/50 hover:text-primary font-label-sm text-label-sm transition-colors whitespace-nowrap">
                    Pecho
                </button>
<button class="shrink-0 px-4 py-2 rounded-full border border-outline-variant bg-surface text-on-surface-variant hover:border-primary/50 hover:text-primary font-label-sm text-label-sm transition-colors whitespace-nowrap">
                    Bíceps
                </button>
<button class="shrink-0 px-4 py-2 rounded-full border border-outline-variant bg-surface text-on-surface-variant hover:border-primary/50 hover:text-primary font-label-sm text-label-sm transition-colors whitespace-nowrap">
                    Muslos
                </button>
<button class="shrink-0 px-4 py-2 rounded-full border border-outline-variant bg-surface text-on-surface-variant hover:border-primary/50 hover:text-primary font-label-sm text-label-sm transition-colors whitespace-nowrap">
                    Cadera
                </button>
</div>
</section>
<!-- Secondary Graph: Cintura -->
<section class="bg-surface-container rounded-xl p-md border border-outline-variant/20 relative">
<div class="mb-4">
<div class="flex justify-between items-start">
<div>
<h3 class="font-label-sm text-label-sm text-on-surface-variant uppercase tracking-widest">Cintura</h3>
<div class="flex items-baseline gap-1 mt-1">
<span class="font-title-md text-title-md text-primary font-bold">82.5</span>
<span class="font-label-sm text-label-sm text-on-surface-variant">cm</span>
</div>
</div>
<span class="font-label-sm text-label-sm text-primary-container flex items-center">
                        -4% <span class="material-symbols-outlined text-[16px] ml-1">trending_down</span>
</span>
</div>
</div>
<!-- Mini Graph -->
<div class="h-24 w-full relative mt-2">
<svg class="w-full h-full overflow-visible" preserveaspectratio="none" viewbox="0 0 100 50">
<polyline class="neon-graph-line" fill="none" points="0,10 25,15 50,30 75,25 100,40" stroke="#63f7ff" stroke-linejoin="round" stroke-width="2"></polyline>
<!-- Points -->
<circle cx="0" cy="10" fill="#101416" r="1.5" stroke="#63f7ff" stroke-width="1"></circle>
<circle cx="25" cy="15" fill="#101416" r="1.5" stroke="#63f7ff" stroke-width="1"></circle>
<circle cx="50" cy="30" fill="#101416" r="1.5" stroke="#63f7ff" stroke-width="1"></circle>
<circle cx="75" cy="25" fill="#101416" r="1.5" stroke="#63f7ff" stroke-width="1"></circle>
<circle class="neon-dot" cx="100" cy="40" fill="#63f7ff" r="2.5"></circle>
</svg>
</div>
</section>
</main>
<!-- BottomNavBar -->
<nav class="fixed bottom-0 w-full z-50 rounded-t-xl bg-surface-container dark:bg-surface-container shadow-lg pt-xs pb-safe px-sm">
<div class="flex justify-around items-center w-full pb-4 pt-2">
<!-- Home (Inactive) -->
<a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-on-surface-variant px-4 py-1 hover:text-primary dark:hover:text-primary transition-all active:scale-90" href="#">
<span class="material-symbols-outlined mb-1">dashboard</span>
<span class="font-label-sm text-label-sm">Home</span>
</a>
<!-- Workouts (Inactive) -->
<a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-on-surface-variant px-4 py-1 hover:text-primary dark:hover:text-primary transition-all active:scale-90" href="#">
<span class="material-symbols-outlined mb-1">fitness_center</span>
<span class="font-label-sm text-label-sm">Workouts</span>
</a>
<!-- Progress (Active) -->
<a class="flex flex-col items-center justify-center bg-primary-container dark:bg-primary-container text-on-primary-container dark:text-on-primary-container rounded-full px-4 py-1 active:scale-90 transition-transform" href="#">
<span class="material-symbols-outlined mb-1" style="font-variation-settings: 'FILL' 1;">insights</span>
<span class="font-label-sm text-label-sm">Progress</span>
</a>
<!-- Profile (Inactive) -->
<a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-on-surface-variant px-4 py-1 hover:text-primary dark:hover:text-primary transition-all active:scale-90" href="#">
<span class="material-symbols-outlined mb-1">person</span>
<span class="font-label-sm text-label-sm">Profile</span>
</a>
</div>
</nav>
</body></html>
