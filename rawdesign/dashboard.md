<!DOCTYPE html>

<html class="dark" lang="en"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>Dashboard</title>
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
                        "secondary-container": "#454749",
                        "error-container": "#93000a",
                        "on-secondary-fixed-variant": "#454749",
                        "primary-container": "#00f5ff",
                        "on-background": "#e0e3e6",
                        "on-tertiary-fixed": "#191c20",
                        "on-secondary-container": "#b4b5b7",
                        "surface-container": "#1c2023",
                        "surface-container-low": "#181c1e",
                        "primary-fixed-dim": "#00dce5",
                        "inverse-surface": "#e0e3e6",
                        "secondary-fixed": "#e2e2e5",
                        "tertiary-fixed": "#e2e2e8",
                        "outline": "#849495",
                        "primary-fixed": "#63f7ff",
                        "on-tertiary-fixed-variant": "#45474c",
                        "surface-container-highest": "#323538",
                        "inverse-primary": "#00696e",
                        "error": "#ffb4ab",
                        "surface-variant": "#323538",
                        "on-tertiary": "#2e3035",
                        "on-error-container": "#ffdad6",
                        "surface-bright": "#363a3c",
                        "on-surface": "#e0e3e6",
                        "secondary": "#c6c6c9",
                        "tertiary": "#faf9ff",
                        "on-secondary": "#2f3133",
                        "on-primary": "#003739",
                        "inverse-on-surface": "#2d3133",
                        "surface-container-lowest": "#0b0f11",
                        "background": "#101416",
                        "surface-tint": "#00dce5",
                        "secondary-fixed-dim": "#c6c6c9",
                        "primary": "#e9feff",
                        "on-error": "#690005",
                        "on-primary-fixed-variant": "#004f53",
                        "surface-dim": "#101416",
                        "surface-container-high": "#272a2d",
                        "on-primary-container": "#006c71",
                        "on-tertiary-container": "#5f6167",
                        "on-secondary-fixed": "#1a1c1e",
                        "tertiary-fixed-dim": "#c5c6cc",
                        "on-primary-fixed": "#002021",
                        "outline-variant": "#3a494a",
                        "tertiary-container": "#dddde4",
                        "surface": "#101416",
                        "on-surface-variant": "#b9caca"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "xl": "32px",
                        "lg": "24px",
                        "gutter": "16px",
                        "unit": "4px",
                        "xs": "4px",
                        "sm": "8px",
                        "margin-edge": "16px",
                        "md": "16px"
                    },
                    "fontFamily": {
                        "headline-lg-mobile": ["Hanken Grotesk"],
                        "label-sm": ["JetBrains Mono"],
                        "headline-lg": ["Hanken Grotesk"],
                        "title-md": ["Inter"],
                        "display-lg": ["Hanken Grotesk"],
                        "body-md": ["Inter"]
                    },
                    "fontSize": {
                        "headline-lg-mobile": ["28px", { "lineHeight": "36px", "fontWeight": "700" }],
                        "label-sm": ["12px", { "lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "500" }],
                        "headline-lg": ["32px", { "lineHeight": "40px", "fontWeight": "700" }],
                        "title-md": ["18px", { "lineHeight": "24px", "fontWeight": "600" }],
                        "display-lg": ["48px", { "lineHeight": "56px", "letterSpacing": "-0.02em", "fontWeight": "800" }],
                        "body-md": ["16px", { "lineHeight": "24px", "fontWeight": "400" }]
                    }
                }
            }
        }
    </script>
<style>
        body {
            background-color: theme('colors.background');
            color: theme('colors.on-background');
            font-family: theme('fontFamily.body-md');
        }
        /* Custom scrollbar for webkit */
        ::-webkit-scrollbar {
            width: 4px;
        }
        ::-webkit-scrollbar-track {
            background: transparent;
        }
        ::-webkit-scrollbar-thumb {
            background: theme('colors.surface-variant');
            border-radius: theme('borderRadius.full');
        }
        
        .module-border {
            border: 1px solid rgba(255, 255, 255, 0.1);
        }
    </style>
<style>
    body {
      min-height: max(884px, 100dvh);
    }
  </style>
  </head>
<body class="antialiased min-h-screen flex flex-col overflow-x-hidden">
<!-- TopAppBar -->
<header class="w-full top-0 sticky bg-background z-40 hidden md:flex">
<div class="flex justify-between items-center px-margin-edge py-sm w-full">
<div class="flex items-center gap-sm">
<img class="w-8 h-8 rounded-full object-cover" data-alt="A small circular avatar placeholder image for a user profile photo. High contrast, clean lighting, minimalist tech aesthetic, dark mode compatible." src="https://lh3.googleusercontent.com/aida-public/AB6AXuD18M1wj14k-RXEXwqRorY1FnE_6wbTsOMGk_05owPQp_YMhx9Tmt326h0UfpG1f-WMUiChAJI-9Hgh6mVPxK1rMXhbrbkvfQDPpb1PZnQJo6YKij5oTG_cOwYR_LHxE2Dd6qKFmKkJ4jzqu7cPuwjZUm243ODPUfV7kRouDqbf_88N8gYggmknP2yW1kcT6OTAy9gSSSigAgClryM6MWcnGJSJsKX6UxXBPsUtIM1deQjzzt7XZsys"/>
<h1 class="font-headline-lg text-headline-lg font-bold text-on-background">Dashboard</h1>
</div>
<button class="p-2 text-primary hover:bg-surface-container-high rounded-full active:scale-95 transition-transform">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">notifications</span>
</button>
</div>
</header>
<!-- Mobile Header -->
<header class="w-full top-0 sticky bg-background z-40 md:hidden border-b border-outline-variant/30">
<div class="flex justify-between items-center px-margin-edge py-sm w-full">
<div class="flex items-center gap-sm">
<img class="w-8 h-8 rounded-full object-cover" data-alt="A small circular avatar placeholder image for a user profile photo. High contrast, clean lighting, minimalist tech aesthetic, dark mode compatible." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCuMJpaCcyU2h3niQW6OTptJs3oJxXXgWEyaKD161TyN0lXKnWbCBSnsBIfYQwpCXktUBy-xJAnFp8QH0IhjxCcno4teq_RYziie8mO8nbLzBC1ji0WEm8X2UWOuJ_yZLodfzl1iTGEMQVkLSXJN7id7cQN31cU9GgppMTVJJEE2Ksn1fcWnsRDA_-oFZe65Fbpe30mbzYurlwhdhfapgYx4_2jEFryyp92c_c7uTGQrEtGWUV8u97F"/>
<h1 class="font-headline-lg-mobile text-headline-lg-mobile font-bold text-on-background">Dashboard</h1>
</div>
<button class="p-2 text-primary hover:bg-surface-container-high rounded-full active:scale-95 transition-transform">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">notifications</span>
</button>
</div>
</header>
<!-- Main Content Canvas -->
<main class="flex-grow px-margin-edge py-gutter pb-32 max-w-4xl mx-auto w-full flex flex-col gap-gutter">
<!-- Welcome Section -->
<section class="py-sm">
<h2 class="font-display-lg text-display-lg text-primary">Hello, Alex!</h2>
<p class="font-body-md text-body-md text-on-surface-variant mt-xs">Ready to crush today's goals.</p>
</section>
<!-- Bento Grid Dashboard -->
<div class="grid grid-cols-1 md:grid-cols-2 gap-gutter">
<!-- Measurement Card -->
<div class="bg-[#faf9ff] text-[#2e3035] rounded-xl p-md module-border flex flex-col justify-between relative overflow-hidden group">
<!-- Subtle glow effect -->
<div class="absolute -right-10 -top-10 w-32 h-32 bg-primary-container/20 rounded-full blur-2xl group-hover:bg-primary-container/30 transition-all duration-500"></div>
<div class="flex justify-between items-start z-10">
<div>
<span class="font-label-sm text-label-sm text-[#5f6167] uppercase">Latest Measurement</span>
<div class="flex items-baseline gap-2 mt-1">
<span class="text-4xl font-bold font-headline-lg-mobile tracking-tight text-[#191c20]">75.4</span>
<span class="font-title-md text-title-md text-[#45474c]">kg</span>
</div>
</div>
<div class="flex items-center gap-1 bg-[#1c2023]/10 px-2 py-1 rounded-full text-[#00696e]">
<span class="material-symbols-outlined text-[16px]">arrow_downward</span>
<span class="font-label-sm text-label-sm font-bold">-0.5kg</span>
</div>
</div>
<!-- Mock Sparkline -->
<div class="mt-lg w-full h-12 flex items-end gap-1 opacity-80 z-10">
<div class="w-1/6 bg-[#b4b5b7] rounded-t-sm h-full"></div>
<div class="w-1/6 bg-[#b4b5b7] rounded-t-sm h-[90%]"></div>
<div class="w-1/6 bg-[#b4b5b7] rounded-t-sm h-[80%]"></div>
<div class="w-1/6 bg-[#b4b5b7] rounded-t-sm h-[85%]"></div>
<div class="w-1/6 bg-[#b4b5b7] rounded-t-sm h-[70%]"></div>
<div class="w-1/6 bg-[#00696e] rounded-t-sm h-[60%] relative">
<div class="absolute -top-2 left-1/2 -translate-x-1/2 w-2 h-2 rounded-full bg-[#00f5ff] shadow-[0_0_8px_#00f5ff]"></div>
</div>
</div>
</div>
<!-- Routine Card -->
<div class="bg-surface-container rounded-xl p-md module-border relative overflow-hidden flex flex-col justify-end min-h-[160px] md:min-h-auto group cursor-pointer transition-transform active:scale-[0.98]">
<!-- Background Image -->
<div class="absolute inset-0 bg-cover bg-center opacity-30 group-hover:opacity-40 transition-opacity duration-300" data-alt="A moody, high-contrast image of gym weights or fitness equipment in a modern gym setting. Dark background with subtle teal and blue lighting accents. Professional, sleek, athletic-tech aesthetic." style="background-image: url('https://lh3.googleusercontent.com/aida-public/AB6AXuAIrDcsmyVbX0FXy9WF5OvkLpM4amHP75GT7-Fae8FDtrtYIsVHjFlp1SI5YH3uJrwZdDR-cFDNfYdFilcTr2u37DTk5VKNaCvT8Jiw5c6cNvvdkMz5C8LGOyj96FBXVVrNvSSduEePiLM1tD_h5t6UYE9emQ_CdZ3EJ6R803YOehKswjPp5Wj2iPCTCOCqMag6HqRXjOzz-3v8UM04crmbTblNfrQqwT_lfoDt74QXwjNht_iNVeh4')"></div>
<div class="absolute inset-0 bg-gradient-to-t from-surface-container-lowest to-transparent"></div>
<div class="z-10 relative">
<span class="font-label-sm text-label-sm text-primary uppercase tracking-widest flex items-center gap-1 mb-1">
<span class="material-symbols-outlined text-[14px]">fitness_center</span> Today's Routine
                    </span>
<h3 class="font-title-md text-title-md text-on-surface">Chest &amp; Triceps Power</h3>
<div class="flex items-center gap-sm mt-2 font-label-sm text-label-sm text-on-surface-variant">
<span class="flex items-center gap-xs"><span class="material-symbols-outlined text-[14px]">schedule</span> 45 mins</span>
<span>•</span>
<span class="flex items-center gap-xs"><span class="material-symbols-outlined text-[14px]">list</span> 8 exercises</span>
</div>
</div>
</div>
<!-- Sync Status -->
<div class="md:col-span-2 bg-[#1c2023]/50 rounded-lg p-sm flex items-center gap-3 module-border border-outline-variant/30">
<div class="w-8 h-8 rounded-full bg-[#00f5ff]/10 flex items-center justify-center text-[#00dce5]">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">cloud_done</span>
</div>
<div>
<p class="font-label-sm text-label-sm text-[#e0e3e6]">Google Drive</p>
<p class="text-[10px] text-[#b9caca] uppercase tracking-wider">Synced (10 mins ago)</p>
</div>
</div>
</div>
<!-- Primary Action Button -->
<div class="mt-auto pt-lg">
<button class="w-full bg-primary text-on-primary py-4 rounded-full font-label-sm text-label-sm uppercase font-bold tracking-widest active:scale-95 transition-transform shadow-[0_4px_14px_rgba(233,254,255,0.1)] hover:bg-[#c6f0f2]">
                Start Today's Workout
            </button>
</div>
</main>
<!-- BottomNavBar -->
<nav class="fixed bottom-0 w-full z-50 rounded-t-xl bg-surface-container border-t border-outline-variant/30 md:hidden">
<div class="flex justify-around items-center px-gutter py-sm w-full max-w-md mx-auto">
<!-- Home (Active) -->
<button class="flex flex-col items-center justify-center bg-primary-container text-on-primary-container rounded-full px-4 py-1 active:scale-90 transition-all duration-200">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">home</span>
<span class="font-label-sm text-[10px] mt-1">Home</span>
</button>
<!-- Workouts -->
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:bg-secondary-container rounded-full px-4 py-1 active:scale-90 transition-all duration-200">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">fitness_center</span>
<span class="font-label-sm text-[10px] mt-1">Workouts</span>
</button>
<!-- Progress -->
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:bg-secondary-container rounded-full px-4 py-1 active:scale-90 transition-all duration-200">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">monitoring</span>
<span class="font-label-sm text-[10px] mt-1">Progress</span>
</button>
<!-- Profile -->
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:bg-secondary-container rounded-full px-4 py-1 active:scale-90 transition-all duration-200">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">person</span>
<span class="font-label-sm text-[10px] mt-1">Profile</span>
</button>
</div>
</nav>
<!-- Desktop SideNav Simulation (Hidden on Mobile) -->
<aside class="hidden md:flex flex-col fixed left-0 top-[68px] h-[calc(100vh-68px)] w-20 border-r border-outline-variant/30 bg-surface-container items-center py-lg gap-lg">
<button class="flex flex-col items-center justify-center bg-primary-container text-on-primary-container rounded-xl w-14 h-14 hover:bg-primary-container/90 transition-colors">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">home</span>
</button>
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:bg-secondary-container rounded-xl w-14 h-14 transition-colors">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">fitness_center</span>
</button>
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:bg-secondary-container rounded-xl w-14 h-14 transition-colors">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">monitoring</span>
</button>
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:bg-secondary-container rounded-xl w-14 h-14 transition-colors mt-auto mb-lg">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">person</span>
</button>
</aside>
</body></html>
