"use strict";(self.webpackChunkdocsite=self.webpackChunkdocsite||[]).push([[649],{3905:(e,t,n)=>{n.d(t,{Zo:()=>u,kt:()=>f});var r=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var c=r.createContext({}),p=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},u=function(e){var t=p(e.components);return r.createElement(c.Provider,{value:t},e.children)},s={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,c=e.parentName,u=l(e,["components","mdxType","originalType","parentName"]),m=p(n),f=o,d=m["".concat(c,".").concat(f)]||m[f]||s[f]||i;return n?r.createElement(d,a(a({ref:t},u),{},{components:n})):r.createElement(d,a({ref:t},u))}));function f(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,a=new Array(i);a[0]=m;var l={};for(var c in t)hasOwnProperty.call(t,c)&&(l[c]=t[c]);l.originalType=e,l.mdxType="string"==typeof e?e:o,a[1]=l;for(var p=2;p<i;p++)a[p]=n[p];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},6544:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>a,default:()=>s,frontMatter:()=>i,metadata:()=>l,toc:()=>p});var r=n(7462),o=(n(7294),n(3905));const i={sidebar_position:5},a="Deployment",l={unversionedId:"tutorial-rabbitmq-platform/deployment",id:"tutorial-rabbitmq-platform/deployment",title:"Deployment",description:"At this point, all the components are defined. The last step is to create an executable for each component and",source:"@site/docs/tutorial-rabbitmq-platform/deployment.md",sourceDirName:"tutorial-rabbitmq-platform",slug:"/tutorial-rabbitmq-platform/deployment",permalink:"/pulverization-framework/docs/tutorial-rabbitmq-platform/deployment",draft:!1,editUrl:"https://github.com/nicolasfara/pulverization-framework/tree/master/docsite/docs/tutorial-rabbitmq-platform/deployment.md",tags:[],version:"current",sidebarPosition:5,frontMatter:{sidebar_position:5},sidebar:"tutorialSidebar",previous:{title:"Create Device Component",permalink:"/pulverization-framework/docs/tutorial-rabbitmq-platform/create-device-component"}},c={},p=[],u={toc:p};function s(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,r.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"deployment"},"Deployment"),(0,o.kt)("p",null,"At this point, all the components are defined. The last step is to create an executable for each component and\ncontainerize it to deploy the system."),(0,o.kt)("p",null,"To do that, we create a simple main function in kotlin with the following elements:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'fun main() = runBlocking {\n  val deviceID = System.getenv("DEVICE_ID")?.toID() ?: error("No `DEVICE_ID` variable found")\n  pulverizationSetup(deviceID, configuration) {\n    registerComponent<DeviceBehaviour>(configuration["device"])\n    registerComponent<DeviceState>(configuration["device"])\n  }\n\n  val behaviour = DeviceBehaviourComponent()\n  behaviour.initialize()\n  behaviour.cycle()\n}\n')),(0,o.kt)("p",null,"First of all, we must specify the ",(0,o.kt)("inlineCode",{parentName:"p"},"DEVICE_ID"),', in this case, the ID is taken by an environment variable.\nLater, we use a DSL for setting up the pulverization framework specifying which components need to be "activated" for\nthat specific component.'),(0,o.kt)("p",null,"After the setup, the only remaining thing to do is create the instance of our ",(0,o.kt)("inlineCode",{parentName:"p"},"DeviceComponent")," in this case\nthe ",(0,o.kt)("inlineCode",{parentName:"p"},"DeviceBehaviourComponent"),", initialize and run it."))}s.isMDXComponent=!0}}]);