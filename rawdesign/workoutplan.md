<!DOCTYPE html>

<html class="dark" lang="en"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>Workout Plans</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;700;800&amp;family=Inter:wght@400;600&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "secondary-fixed": "#e2e2e5",
                        "surface-bright": "#363a3c",
                        "tertiary-fixed-dim": "#c5c6cc",
                        "background": "#101416",
                        "secondary-fixed-dim": "#c6c6c9",
                        "secondary-container": "#454749",
                        "surface-container-low": "#181c1e",
                        "error": "#ffb4ab",
                        "tertiary-container": "#dddde4",
                        "surface": "#101416",
                        "on-tertiary": "#2e3035",
                        "on-surface": "#e0e3e6",
                        "tertiary-fixed": "#e2e2e8",
                        "on-primary-container": "#006c71",
                        "on-secondary": "#2f3133",
                        "primary-fixed": "#63f7ff",
                        "on-tertiary-fixed": "#191c20",
                        "surface-tint": "#00dce5",
                        "on-secondary-fixed-variant": "#454749",
                        "on-primary-fixed": "#002021",
                        "on-surface-variant": "#b9caca",
                        "surface-container-high": "#272a2d",
                        "error-container": "#93000a",
                        "primary": "#e9feff",
                        "inverse-surface": "#e0e3e6",
                        "on-primary": "#003739",
                        "outline-variant": "#3a494a",
                        "inverse-primary": "#00696e",
                        "inverse-on-surface": "#2d3133",
                        "on-error": "#690005",
                        "primary-fixed-dim": "#00dce5",
                        "on-tertiary-fixed-variant": "#45474c",
                        "on-background": "#e0e3e6",
                        "tertiary": "#faf9ff",
                        "secondary": "#c6c6c9",
                        "surface-container": "#1c2023",
                        "surface-dim": "#101416",
                        "outline": "#849495",
                        "on-tertiary-container": "#5f6167",
                        "surface-container-lowest": "#0b0f11",
                        "surface-variant": "#323538",
                        "on-secondary-container": "#b4b5b7",
                        "on-secondary-fixed": "#1a1c1e",
                        "surface-container-highest": "#323538",
                        "on-error-container": "#ffdad6",
                        "on-primary-fixed-variant": "#004f53",
                        "primary-container": "#00f5ff"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "lg": "24px",
                        "margin-edge": "16px",
                        "sm": "8px",
                        "xs": "4px",
                        "unit": "4px",
                        "gutter": "16px",
                        "md": "16px",
                        "xl": "32px"
                    },
                    "fontFamily": {
                        "display-lg": [
                            "Hanken Grotesk"
                        ],
                        "title-md": [
                            "Inter"
                        ],
                        "headline-lg-mobile": [
                            "Hanken Grotesk"
                        ],
                        "body-md": [
                            "Inter"
                        ],
                        "label-sm": [
                            "JetBrains Mono"
                        ],
                        "headline-lg": [
                            "Hanken Grotesk"
                        ]
                    },
                    "fontSize": {
                        "display-lg": [
                            "48px",
                            {
                                "lineHeight": "56px",
                                "letterSpacing": "-0.02em",
                                "fontWeight": "800"
                            }
                        ],
                        "title-md": [
                            "18px",
                            {
                                "lineHeight": "24px",
                                "fontWeight": "600"
                            }
                        ],
                        "headline-lg-mobile": [
                            "28px",
                            {
                                "lineHeight": "36px",
                                "fontWeight": "700"
                            }
                        ],
                        "body-md": [
                            "16px",
                            {
                                "lineHeight": "24px",
                                "fontWeight": "400"
                            }
                        ],
                        "label-sm": [
                            "12px",
                            {
                                "lineHeight": "16px",
                                "letterSpacing": "0.05em",
                                "fontWeight": "500"
                            }
                        ],
                        "headline-lg": [
                            "32px",
                            {
                                "lineHeight": "40px",
                                "fontWeight": "700"
                            }
                        ]
                    }
                },
            },
        }
    </script>
<style>
        .fab-menu {
            transform: scale(0);
            opacity: 0;
            transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
            transform-origin: bottom right;
        }
        .fab-menu.open {
            transform: scale(1);
            opacity: 1;
        }
        .fab-icon-spin {
            transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }
        .fab-active .fab-icon-spin {
            transform: rotate(45deg);
        }
    </style>
<style>
    body {
      min-height: max(884px, 100dvh);
    }
  </style>
  </head>
<body class="bg-background text-on-background font-body-md text-body-md min-h-screen pb-24 md:pb-0">
<!-- TopAppBar -->
<header class="w-full top-0 sticky bg-background dark:bg-background z-40">
<div class="flex justify-between items-center px-margin-edge py-sm w-full bg-background dark:bg-background">
<div class="flex items-center gap-sm">
<div class="w-10 h-10 rounded-full overflow-hidden bg-surface-container-high border border-outline-variant">
<img class="w-full h-full object-cover" data-alt="A futuristic fitness athlete in a dark, high-tech gym setting, lit by neon cyan and deep blue lights. The athlete appears focused and energetic, fitting a modern athletic-tech app aesthetic." src="https://lh3.googleusercontent.com/aida-public/AB6AXuDH-TYLgaywIgN95yTmM_DbT6h3gDUJ-zCh_5OJfRpiT8YVazCvaxN4I9Rn9rore4pP332ARl52cs1RXtWUnXzkHJIUpOGO-PCNG4u8vdN_00mEYvJ4XilICHscxrumR4PcCAP6GIBBxUd99ul5whBEv_M3i1bZeU0Y7-heEg4lZoLDXA7x_zlbo5nbxbcVx1oOG8ZzrDS7O2z_YUMoBJr1yJij2LXFmoZvxzvHRlSeiqTRZeOQU63Y"/>
</div>
</div>
<h1 class="font-headline-lg-mobile text-headline-lg-mobile font-bold text-on-background dark:text-on-background">Mis Planes</h1>
<div class="flex items-center">
<button class="p-2 rounded-full hover:bg-surface-container-high dark:hover:bg-surface-container-high active:scale-95 transition-transform text-primary dark:text-primary-fixed-dim">
<span class="material-symbols-outlined" data-icon="notifications">notifications</span>
</button>
</div>
</div>
</header>
<main class="px-margin-edge py-md flex flex-col gap-lg max-w-7xl mx-auto w-full">
<!-- Planes Activos Section -->
<section class="flex flex-col gap-sm">
<h2 class="font-title-md text-title-md text-on-surface mb-xs">Planes Activos</h2>
<div class="grid grid-cols-1 md:grid-cols-2 gap-gutter">
<!-- Active Card 1 -->
<div class="bg-surface-container rounded-xl p-md border border-outline-variant/30 flex flex-col gap-md relative overflow-hidden group">
<div class="absolute inset-0 bg-gradient-to-br from-primary-container/5 to-transparent pointer-events-none"></div>
<div class="flex justify-between items-start z-10">
<div>
<h3 class="font-headline-lg-mobile text-headline-lg-mobile font-bold text-on-surface mb-xs group-hover:text-primary-container transition-colors">Hipertrofia Total</h3>
<div class="flex items-center gap-sm font-label-sm text-label-sm text-on-surface-variant uppercase">
<span class="flex items-center gap-1"><span class="material-symbols-outlined text-[16px]">calendar_today</span> 5 days/week</span>
<span>•</span>
<span class="flex items-center gap-1"><span class="material-symbols-outlined text-[16px]">timer</span> 8 weeks</span>
</div>
</div>
<div class="bg-primary-container/20 text-primary-container px-2 py-1 rounded-md font-label-sm text-label-sm flex items-center gap-1 border border-primary-container/30">
<span class="material-symbols-outlined text-[14px]">auto_awesome</span> Generado por IA
                        </div>
</div>
<div class="flex items-end justify-between mt-sm z-10">
<div class="flex flex-col">
<span class="font-label-sm text-label-sm text-on-surface-variant uppercase mb-1">Level</span>
<span class="font-title-md text-title-md text-on-surface">Advanced</span>
</div>
<button class="bg-primary-container text-on-primary-container px-4 py-2 rounded-lg font-label-sm text-label-sm uppercase hover:bg-surface-tint transition-colors">
                            Continue
                        </button>
</div>
</div>
<!-- Active Card 2 -->
<div class="bg-surface-container rounded-xl p-md border border-outline-variant/30 flex flex-col gap-md relative overflow-hidden group">
<div class="flex justify-between items-start z-10">
<div>
<h3 class="font-headline-lg-mobile text-headline-lg-mobile font-bold text-on-surface mb-xs group-hover:text-primary-container transition-colors">Push/Pull/Legs</h3>
<div class="flex items-center gap-sm font-label-sm text-label-sm text-on-surface-variant uppercase">
<span class="flex items-center gap-1"><span class="material-symbols-outlined text-[16px]">calendar_today</span> 6 days/week</span>
<span>•</span>
<span class="flex items-center gap-1"><span class="material-symbols-outlined text-[16px]">timer</span> 12 weeks</span>
</div>
</div>
</div>
<div class="flex items-end justify-between mt-sm z-10">
<div class="flex flex-col">
<span class="font-label-sm text-label-sm text-on-surface-variant uppercase mb-1">Level</span>
<span class="font-title-md text-title-md text-on-surface">Intermediate</span>
</div>
<button class="border border-primary-container text-primary-container px-4 py-2 rounded-lg font-label-sm text-label-sm uppercase hover:bg-primary-container/10 transition-colors">
                            View Plan
                        </button>
</div>
</div>
</div>
</section>
<!-- Archivo Section -->
<section class="flex flex-col gap-sm opacity-80 mt-md">
<h2 class="font-title-md text-title-md text-on-surface-variant mb-xs flex items-center gap-2">
<span class="material-symbols-outlined">inventory_2</span> Archivo
            </h2>
<div class="grid grid-cols-1 md:grid-cols-3 gap-gutter">
<!-- Inactive Card 1 -->
<div class="bg-surface-container-low rounded-xl p-md border border-outline-variant/20 flex flex-col gap-sm grayscale-[30%]">
<h3 class="font-title-md text-title-md text-on-surface line-through decoration-outline-variant">Fuerza Base 5x5</h3>
<div class="flex items-center gap-sm font-label-sm text-label-sm text-on-surface-variant uppercase">
<span class="flex items-center gap-1"><span class="material-symbols-outlined text-[14px]">calendar_today</span> 3 days/week</span>
</div>
<div class="mt-sm pt-sm border-t border-outline-variant/20 font-label-sm text-label-sm text-on-surface-variant">
                        Completed: 12 Oct 2023
                    </div>
</div>
<!-- Inactive Card 2 -->
<div class="bg-surface-container-low rounded-xl p-md border border-outline-variant/20 flex flex-col gap-sm grayscale-[30%]">
<h3 class="font-title-md text-title-md text-on-surface">Core Intensivo</h3>
<div class="flex items-center gap-sm font-label-sm text-label-sm text-on-surface-variant uppercase">
<span class="flex items-center gap-1"><span class="material-symbols-outlined text-[14px]">calendar_today</span> 2 days/week</span>
</div>
<div class="mt-sm pt-sm border-t border-outline-variant/20 font-label-sm text-label-sm text-on-surface-variant">
                        Abandoned: 05 Sep 2023
                    </div>
</div>
</div>
</section>
</main>
<!-- Floating Action Button -->
<div class="fixed bottom-24 right-margin-edge md:bottom-margin-edge z-50 flex flex-col items-end gap-sm" id="fab-container">
<!-- FAB Menu -->
<div class="fab-menu flex flex-col gap-sm mb-2" id="fab-menu">
<button class="flex items-center gap-3 bg-surface-container-high border border-outline-variant px-4 py-2 rounded-full hover:bg-secondary-container transition-colors shadow-lg group">
<span class="font-label-sm text-label-sm text-on-surface uppercase group-hover:text-primary-container transition-colors">Crear con IA</span>
<div class="bg-primary-container text-on-primary-container w-8 h-8 rounded-full flex items-center justify-center">
<span class="material-symbols-outlined text-[18px]" data-icon="auto_awesome">auto_awesome</span>
</div>
</button>
<button class="flex items-center gap-3 bg-surface-container-high border border-outline-variant px-4 py-2 rounded-full hover:bg-secondary-container transition-colors shadow-lg group">
<span class="font-label-sm text-label-sm text-on-surface uppercase group-hover:text-primary-container transition-colors">Plan Manual</span>
<div class="bg-surface-variant text-on-surface w-8 h-8 rounded-full flex items-center justify-center border border-outline-variant">
<span class="material-symbols-outlined text-[18px]" data-icon="edit_document">edit_document</span>
</div>
</button>
</div>
<!-- Main FAB -->
<button class="w-14 h-14 bg-primary-container text-on-primary-container rounded-full flex items-center justify-center shadow-[0_0_20px_rgba(0,245,255,0.3)] hover:scale-105 active:scale-95 transition-all" id="main-fab">
<span class="material-symbols-outlined text-[28px] fab-icon-spin" data-icon="add">add</span>
</button>
</div>
<!-- BottomNavBar -->
<nav class="fixed bottom-0 w-full z-50 rounded-t-xl bg-surface-container dark:bg-surface-container border-t border-outline-variant dark:border-outline-variant md:hidden">
<div class="flex justify-around items-center px-gutter py-sm">
<!-- Home -->
<button class="flex flex-col items-center justify-center text-on-surface-variant dark:text-on-surface-variant px-4 py-1 hover:bg-secondary-container dark:hover:bg-secondary-container active:scale-90 transition-all duration-200 rounded-full">
<span class="material-symbols-outlined mb-1" data-icon="home">home</span>
<span class="font-label-sm text-label-sm">Home</span>
</button>
<!-- Workouts (Active) -->
<button class="flex flex-col items-center justify-center bg-primary-container text-on-primary-container rounded-full px-4 py-1 active:scale-90 transition-all duration-200">
<span class="material-symbols-outlined mb-1" data-icon="fitness_center" data-weight="fill" style="font-variation-settings: 'FILL' 1;">fitness_center</span>
<span class="font-label-sm text-label-sm">Workouts</span>
</button>
<!-- Progress -->
<button class="flex flex-col items-center justify-center text-on-surface-variant dark:text-on-surface-variant px-4 py-1 hover:bg-secondary-container dark:hover:bg-secondary-container active:scale-90 transition-all duration-200 rounded-full">
<span class="material-symbols-outlined mb-1" data-icon="monitoring">monitoring</span>
<span class="font-label-sm text-label-sm">Progress</span>
</button>
<!-- Profile -->
<button class="flex flex-col items-center justify-center text-on-surface-variant dark:text-on-surface-variant px-4 py-1 hover:bg-secondary-container dark:hover:bg-secondary-container active:scale-90 transition-all duration-200 rounded-full">
<span class="material-symbols-outlined mb-1" data-icon="person">person</span>
<span class="font-label-sm text-label-sm">Profile</span>
</button>
</div>
</nav>
<script>
        document.addEventListener('DOMContentLoaded', () => {
            const mainFab = document.getElementById('main-fab');
            const fabMenu = document.getElementById('fab-menu');
            const fabContainer = document.getElementById('fab-container');

            mainFab.addEventListener('click', () => {
                fabContainer.classList.toggle('fab-active');
                fabMenu.classList.toggle('open');
            });

            // Close FAB menu when clicking outside
            document.addEventListener('click', (event) => {
                if (!fabContainer.contains(event.target) && fabContainer.classList.contains('fab-active')) {
                    fabContainer.classList.remove('fab-active');
                    fabMenu.classList.remove('open');
                }
            });
        });
    </script>
</body></html>
