/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt.chart;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.lang.ref.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.*;
import javax.servlet.*;
import eu.webtoolkit.jwt.*;
import eu.webtoolkit.jwt.chart.*;
import eu.webtoolkit.jwt.utils.*;
import eu.webtoolkit.jwt.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for iterating over series data in a chart.
 * <p>
 * 
 * This class is specialized for rendering series data.
 * <p>
 */
public class SeriesIterator {
	private static Logger logger = LoggerFactory
			.getLogger(SeriesIterator.class);

	/**
	 * Start handling a new segment.
	 * <p>
	 * Because of a &apos;break&apos; specified in an axis, axes may be divided
	 * in one or two segments (in fact only the API limits this now to two). The
	 * iterator will iterate all segments seperately, but each time with a
	 * different clipping region specified in the painter, corresponding to that
	 * segment.
	 * <p>
	 * The <i>currentSegmentArea</i> specifies the clipping area.
	 */
	public void startSegment(int currentXSegment, int currentYSegment,
			final WRectF currentSegmentArea) {
		this.currentXSegment_ = currentXSegment;
		this.currentYSegment_ = currentYSegment;
	}

	/**
	 * End handling a particular segment.
	 * <p>
	 * 
	 * @see SeriesIterator#startSegment(int currentXSegment, int
	 *      currentYSegment, WRectF currentSegmentArea)
	 */
	public void endSegment() {
	}

	/**
	 * Start iterating a particular series.
	 * <p>
	 * Returns whether the series values should be iterated. The
	 * <i>groupWidth</i> is the width (in pixels) of a single bar group. The
	 * chart contains <i>numBarGroups</i>, and the current series is in the
	 * <i>currentBarGroup</i>&apos;th group.
	 */
	public boolean startSeries(final WDataSeries series, double groupWidth,
			int numBarGroups, int currentBarGroup) {
		return true;
	}

	/**
	 * End iterating a particular series.
	 */
	public void endSeries() {
	}

	/**
	 * Process a value.
	 * <p>
	 * Processes a value with model coordinates (<i>x</i>, <i>y</i>). The y
	 * value may differ from the model&apos;s y value, because of stacked
	 * series. The y value here corresponds to the location on the chart, after
	 * stacking.
	 * <p>
	 * The <i>stackY</i> argument is the y value from the previous series (also
	 * after stacking). It will be 0, unless this series is stacked.
	 */
	public void newValue(final WDataSeries series, double x, double y,
			double stackY, final WModelIndex xIndex, final WModelIndex yIndex) {
	}

	/**
	 * Returns the current X segment.
	 */
	public int getCurrentXSegment() {
		return this.currentXSegment_;
	}

	/**
	 * Returns the current Y segment.
	 */
	public int getCurrentYSegment() {
		return this.currentYSegment_;
	}

	public static void setPenColor(final WPen pen, final WModelIndex xIndex,
			final WModelIndex yIndex, int colorRole) {
		Object color = new Object();
		if ((yIndex != null)) {
			color = yIndex.getData(colorRole);
		}
		if ((color == null) && (xIndex != null)) {
			color = xIndex.getData(colorRole);
		}
		if (!(color == null)) {
			pen.setColor((WColor) color);
		}
	}

	public static void setBrushColor(final WBrush brush,
			final WModelIndex xIndex, final WModelIndex yIndex, int colorRole) {
		Object color = new Object();
		if ((yIndex != null)) {
			color = yIndex.getData(colorRole);
		}
		if ((color == null) && (xIndex != null)) {
			color = xIndex.getData(colorRole);
		}
		if (!(color == null)) {
			brush.setColor((WColor) color);
		}
	}

	private int currentXSegment_;
	private int currentYSegment_;

	static WJavaScriptPreamble wtjs1() {
		return new WJavaScriptPreamble(
				JavaScriptScope.WtClassScope,
				JavaScriptObjectType.JavaScriptConstructor,
				"WCartesianChart",
				"function(E,v,y,d){function Y(){return d.crosshair||d.followCurve!==-1}function sa(a){return a.pointerType===2||a.pointerType===3||a.pointerType===\"pen\"||a.pointerType===\"touch\"}function j(a){if(a===h)return d.xTransform;if(a===i)return d.yTransform}function Z(){if(d.isHorizontal){var a=p(d.area),b=q(d.area);return C([0,1,1,0,a,b],C(j(h),C(j(i),[0,1,1,0,-b,-a])))}else{a=p(d.area);b=r(d.area);return C([1,0,0,-1,a,b],C(j(h),C(j(i),[1,0,0, -1,-a,b])))}}function G(){return C(Z(),d.area)}function P(a,b){if(b===undefined)b=false;a=b?a:C(ta(Z()),a);a=d.isHorizontal?[(a[i]-d.area[1])/d.area[3],(a[h]-d.area[0])/d.area[2]]:[(a[h]-d.area[0])/d.area[2],1-(a[i]-d.area[1])/d.area[3]];return[d.modelArea[0]+a[h]*d.modelArea[2],d.modelArea[1]+a[i]*d.modelArea[3]]}function W(a,b){if(b===undefined)b=false;if(d.isHorizontal){a=[(a[h]-d.modelArea[0])/d.modelArea[2],(a[i]-d.modelArea[1])/d.modelArea[3]];a=[d.area[0]+a[i]*d.area[2],d.area[1]+a[h]*d.area[3]]}else{a= [(a[h]-d.modelArea[0])/d.modelArea[2],1-(a[i]-d.modelArea[1])/d.modelArea[3]];a=[d.area[0]+a[h]*d.area[2],d.area[1]+a[i]*d.area[3]]}return b?a:C(Z(),a)}function Ba(a,b){return b[0][a]<b[b.length-1][a]}function Ia(a,b){var f=h;if(d.isHorizontal)f=i;var e=Ba(f,b),g=ka(a,b,e);if(g<0)g=0;if(g>=b.length)return[b[b.length-1][h],b[b.length-1][i]];if(g>=b.length)g=b.length-2;if(b[g][f]===a)return[b[g][h],b[g][i]];var k=e?g+1:g-1;if(e&&b[k][2]==X)k+=2;if(!e&&k<0)return[b[g][h],b[g][i]];if(!e&&k>0&&b[k][2]== aa)k-=2;e=Math.abs(a-b[g][f]);a=Math.abs(b[k][f]-a);return e<a?[b[g][h],b[g][i]]:[b[k][h],b[k][i]]}function ka(a,b,f){function e(u){return f?b[u]:b[m-1-u]}function g(u){for(;e(u)[2]===X||e(u)[2]===aa;)u--;return u}var k=h;if(d.isHorizontal)k=i;var m=b.length,n=Math.floor(m/2);n=g(n);var o=0,z=m,s=false;if(e(0)[k]>a)return f?-1:m;if(e(m-1)[k]<a)return f?m:-1;for(;!s;){var x=n+1;if(e(x)[2]===X||e(x)[2]===aa)x+=2;if(e(n)[k]>a){z=n;n=Math.floor((z+o)/2);n=g(n)}else if(e(n)[k]===a)s=true;else if(e(x)[k]> a)s=true;else if(e(x)[k]===a){n=x;s=true}else{o=n;n=Math.floor((z+o)/2);n=g(n)}}return f?n:m-1-n}function ha(){var a,b;if(d.isHorizontal){a=(P([0,q(d.area)])[0]-d.modelArea[0])/d.modelArea[2];b=(P([0,r(d.area)])[0]-d.modelArea[0])/d.modelArea[2]}else{a=(P([p(d.area),0])[0]-d.modelArea[0])/d.modelArea[2];b=(P([t(d.area),0])[0]-d.modelArea[0])/d.modelArea[2]}var f;for(f=0;f<d.sliders.length;++f){var e=$(\"#\"+d.sliders[f]);if(e)(e=e.data(\"sobj\"))&&e.changeRange(a,b)}}function ba(){Q&&Ca(function(){y.repaint(); Y()&&ua()})}function ua(){if(Q){var a=D.getContext(\"2d\");a.clearRect(0,0,D.width,D.height);a.save();a.beginPath();a.moveTo(p(d.area),q(d.area));a.lineTo(t(d.area),q(d.area));a.lineTo(t(d.area),r(d.area));a.lineTo(p(d.area),r(d.area));a.closePath();a.clip();var b=C(ta(Z()),w),f=w[h],e=w[i];if(d.followCurve!==-1){b=Ia(d.isHorizontal?b[i]:b[h],d.series[d.followCurve]);e=C(Z(),b);f=e[h];e=e[i];w[h]=f;w[i]=e}b=d.isHorizontal?[(b[i]-d.area[1])/d.area[3],(b[h]-d.area[0])/d.area[2]]:[(b[h]-d.area[0])/d.area[2], 1-(b[i]-d.area[1])/d.area[3]];b=[d.modelArea[0]+b[h]*d.modelArea[2],d.modelArea[1]+b[i]*d.modelArea[3]];a.font=\"16px sans-serif\";a.textAlign=\"right\";a.textBaseline=\"top\";var g=b[0].toFixed(2);b=b[1].toFixed(2);if(g==\"-0.00\")g=\"0.00\";if(b==\"-0.00\")b=\"0.00\";a.fillText(\"(\"+g+\",\"+b+\")\",t(d.area)-d.coordinateOverlayPadding[0],q(d.area)+d.coordinateOverlayPadding[1]);a.setLineDash&&a.setLineDash([1,2]);a.beginPath();a.moveTo(Math.floor(f)+0.5,Math.floor(q(d.area))+0.5);a.lineTo(Math.floor(f)+0.5,Math.floor(r(d.area))+ 0.5);a.moveTo(Math.floor(p(d.area))+0.5,Math.floor(e)+0.5);a.lineTo(Math.floor(t(d.area))+0.5,Math.floor(e)+0.5);a.stroke();a.restore()}}function ca(a,b){var f;if(a.x!==undefined){f=a.x;a=a.y}else{f=a[0];a=a[1]}return f>=p(b)&&f<=t(b)&&a>=q(b)&&a<=r(b)}function Ja(a){return q(a)<=q(d.area)+la&&r(a)>=r(d.area)-la&&p(a)<=p(d.area)+la&&t(a)>=t(d.area)-la}function K(a){var b=G();if(d.isHorizontal)if(a===da)a=ea;else if(a===ea)a=da;if(a===undefined||a===da)if(j(h)[0]<1){j(h)[0]=1;b=G()}if(a===undefined|| a===ea)if(j(i)[3]<1){j(i)[3]=1;b=G()}if(a===undefined||a===da){if(p(b)>p(d.area)){b=p(d.area)-p(b);if(d.isHorizontal)j(i)[5]=j(i)[5]+b;else j(h)[4]=j(h)[4]+b;b=G()}if(t(b)<t(d.area)){b=t(d.area)-t(b);if(d.isHorizontal)j(i)[5]=j(i)[5]+b;else j(h)[4]=j(h)[4]+b;b=G()}}if(a===undefined||a===ea){if(q(b)>q(d.area)){b=q(d.area)-q(b);if(d.isHorizontal)j(h)[4]=j(h)[4]+b;else j(i)[5]=j(i)[5]-b;b=G()}if(r(b)<r(d.area)){b=r(d.area)-r(b);if(d.isHorizontal)j(h)[4]=j(h)[4]+b;else j(i)[5]=j(i)[5]-b;G()}}}function Ka(){if(Y&& (D===undefined||y.canvas.width!==D.width||y.canvas.height!==D.height)){if(D){D.parentNode.removeChild(D);jQuery.removeData(v,\"oobj\");D=undefined}c=document.createElement(\"canvas\");c.setAttribute(\"width\",y.canvas.width);c.setAttribute(\"height\",y.canvas.height);c.style.position=\"absolute\";c.style.display=\"block\";c.style.left=\"0\";c.style.top=\"0\";if(window.MSPointerEvent||window.PointerEvent){c.style.msTouchAction=\"none\";c.style.touchAction=\"none\"}y.canvas.parentNode.appendChild(c);D=c;jQuery.data(v, \"oobj\",D)}else if(D!==undefined&&!Y()){D.parentNode.removeChild(D);jQuery.removeData(v,\"oobj\");D=undefined}if(w===null)w=W([(p(d.modelArea)+t(d.modelArea))/2,(q(d.modelArea)+r(d.modelArea))/2])}function La(a,b){var f=Math.cos(a);a=Math.sin(a);var e=f*a,g=-b[0]*f-b[1]*a;return[f*f,e,e,a*a,f*g+b[0],a*g+b[1]]}function Ma(a,b,f){a=[b[h]-a[h],b[i]-a[i]];return f*f>=a[h]*a[h]+a[i]*a[i]}function va(a,b){if(fa){var f=Date.now();if(b===undefined)b=f-M;var e={x:0,y:0},g=G(),k=Na;if(b>2*ia){Q=false;var m=Math.floor(b/ ia-1),n;for(n=0;n<m;++n){va(a,ia);if(!fa){Q=true;ba();return}}b-=m*ia;Q=true}if(l.x===Infinity||l.x===-Infinity)l.x=l.x>0?S:-S;if(isFinite(l.x)){l.x/=1+Da*b;g[0]+=l.x*b;if(p(g)>p(d.area)){l.x+=-k*(p(g)-p(d.area))*b;l.x*=0.7}else if(t(g)<t(d.area)){l.x+=-k*(t(g)-t(d.area))*b;l.x*=0.7}if(Math.abs(l.x)<wa)if(p(g)>p(d.area))l.x=wa;else if(t(g)<t(d.area))l.x=-wa;if(Math.abs(l.x)>S)l.x=(l.x>0?1:-1)*S;e.x=l.x*b}if(l.y===Infinity||l.y===-Infinity)l.y=l.y>0?S:-S;if(isFinite(l.y)){l.y/=1+Da*b;g[1]+=l.y*b;if(q(g)> q(d.area)){l.y+=-k*(q(g)-q(d.area))*b;l.y*=0.7}else if(r(g)<r(d.area)){l.y+=-k*(r(g)-r(d.area))*b;l.y*=0.7}if(Math.abs(l.y)<0.001)if(q(g)>q(d.area))l.y=0.001;else if(r(g)<r(d.area))l.y=-0.001;if(Math.abs(l.y)>S)l.y=(l.y>0?1:-1)*S;e.y=l.y*b}g=G();I(e,ga);a=G();if(p(g)>p(d.area)&&p(a)<=p(d.area)){l.x=0;I({x:-e.x,y:0},ga);K(da)}if(t(g)<t(d.area)&&t(a)>=t(d.area)){l.x=0;I({x:-e.x,y:0},ga);K(da)}if(q(g)>q(d.area)&&q(a)<=q(d.area)){l.y=0;I({x:0,y:-e.y},ga);K(ea)}if(r(g)<r(d.area)&&r(a)>=r(d.area)){l.y= 0;I({x:0,y:-e.y},ga);K(ea)}if(Math.abs(l.x)<Ea&&Math.abs(l.y)<Ea&&Ja(a)){K();fa=false;F=null;l.x=0;l.y=0;M=null;A=[]}else{M=f;Q&&ma(va)}}}function Fa(a){return Math.floor(Math.log(a)/Math.LN2+0.5)+1}function na(){var a,b,f=Fa(j(h)[0])-1;if(f>=d.pens.x.length)f=d.pens.x.length-1;for(a=0;a<d.pens.x.length;++a)if(f===a)for(b=0;b<d.pens.x[a].length;++b)d.pens.x[a][b].color[3]=d.penAlpha.x[b];else for(b=0;b<d.pens.x[a].length;++b)d.pens.x[a][b].color[3]=0;f=Fa(j(i)[3])-1;if(f>=d.pens.y.length)f=d.pens.y.length- 1;for(a=0;a<d.pens.y.length;++a)if(f===a)for(b=0;b<d.pens.y[a].length;++b)d.pens.y[a][b].color[3]=d.penAlpha.y[b];else for(b=0;b<d.pens.y[a].length;++b)d.pens.y[a][b].color[3]=0}function I(a,b){var f=P(w);if(d.isHorizontal)a={x:a.y,y:-a.x};if(b&ga){j(h)[4]=j(h)[4]+a.x;j(i)[5]=j(i)[5]-a.y}else if(b&Ga){b=G();if(p(b)>p(d.area)){if(a.x>0)a.x/=1+(p(b)-p(d.area))*oa}else if(t(b)<t(d.area))if(a.x<0)a.x/=1+(t(d.area)-t(b))*oa;if(q(b)>q(d.area)){if(a.y>0)a.y/=1+(q(b)-q(d.area))*oa}else if(r(b)<r(d.area))if(a.y< 0)a.y/=1+(r(d.area)-r(b))*oa;j(h)[4]=j(h)[4]+a.x;j(i)[5]=j(i)[5]-a.y;w[h]+=a.x;w[i]+=a.y}else{j(h)[4]=j(h)[4]+a.x;j(i)[5]=j(i)[5]-a.y;w[h]+=a.x;w[i]+=a.y;K()}a=W(f);w[h]=a[h];w[i]=a[i];ba();ha()}function ja(a,b,f){var e=P(w),g;g=d.isHorizontal?[a.y-q(d.area),a.x-p(d.area)]:C(ta([1,0,0,-1,p(d.area),r(d.area)]),[a.x,a.y]);a=g[0];g=g[1];var k=Math.pow(1.2,d.isHorizontal?f:b);b=Math.pow(1.2,d.isHorizontal?b:f);if(j(h)[0]*k>d.maxZoom[h])k=d.maxZoom[h]/j(h)[0];if(k<1||j(h)[0]!==d.maxZoom[h])pa(j(h),C([k, 0,0,1,a-k*a,0],j(h)));if(j(i)[3]*b>d.maxZoom[i])b=d.maxZoom[i]/j(i)[3];if(b<1||j(i)[3]!==d.maxZoom[i])pa(j(i),C([1,0,0,b,0,g-b*g],j(i)));K();e=W(e);w[h]=e[h];w[i]=e[i];na();ba();ha()}var ia=17,ma=function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||function(a){window.setTimeout(a,ia)}}(),xa=false,Ca=function(a){if(!xa){xa=true;ma(function(){a();xa=false})}};if(window.MSPointerEvent||window.PointerEvent){v.style.touchAction=\"none\";y.canvas.style.msTouchAction= \"none\";y.canvas.style.touchAction=\"none\"}var X=2,aa=3,ga=1,Ga=2,da=1,ea=2,h=0,i=1,J={},N=false;if(window.MSPointerEvent||window.PointerEvent)(function(){function a(){if(pointers.length>0&&!N)N=true;else if(pointers.length<=0&&N)N=false}function b(k){if(sa(k)){k.preventDefault();pointers.push(k);a();J.start(v,{touches:pointers.slice(0)})}}function f(k){if(N)if(sa(k)){k.preventDefault();var m;for(m=0;m<pointers.length;++m)if(pointers[m].pointerId===k.pointerId){pointers.splice(m,1);break}a();J.end(v, {touches:pointers.slice(0),changedTouches:[]})}}function e(k){if(sa(k)){k.preventDefault();var m;for(m=0;m<pointers.length;++m)if(pointers[m].pointerId===k.pointerId){pointers[m]=k;break}a();J.moved(v,{touches:pointers.slice(0)})}}pointers=[];var g=jQuery.data(v,\"eobj\");if(g)if(window.PointerEvent){v.removeEventListener(\"pointerdown\",g.pointerDown);v.removeEventListener(\"pointerup\",g.pointerUp);v.removeEventListener(\"pointerout\",g.pointerUp);v.removeEventListener(\"pointermove\",g.pointerMove)}else{v.removeEventListener(\"MSPointerDown\", g.pointerDown);v.removeEventListener(\"MSPointerUp\",g.pointerUp);v.removeEventListener(\"MSPointerOut\",g.pointerUp);v.removeEventListener(\"MSPointerMove\",g.pointerMove)}jQuery.data(v,\"eobj\",{pointerDown:b,pointerUp:f,pointerMove:e});if(window.PointerEvent){v.addEventListener(\"pointerdown\",b);v.addEventListener(\"pointerup\",f);v.addEventListener(\"pointerout\",f);v.addEventListener(\"pointermove\",e)}else{v.addEventListener(\"MSPointerDown\",b);v.addEventListener(\"MSPointerUp\",f);v.addEventListener(\"MSPointerOut\", f);v.addEventListener(\"MSPointerMove\",e)}})();var Da=0.003,Na=2.0E-4,oa=0.07,la=3,wa=0.001,S=1.5,Ea=0.02;jQuery.data(v,\"cobj\",this);var T=this,B=E.WT;T.config=d;var D=jQuery.data(v,\"oobj\"),w=null,Q=true,F=null,A=[],U=false,V=false,H=null,ya=null,za=null,l={x:0,y:0},M=null,qa=null;E=B.gfxUtils;var C=E.transform_mult,ta=E.transform_inverted,pa=E.transform_assign,q=E.rect_top,r=E.rect_bottom,p=E.rect_left,t=E.rect_right,fa=false;y.combinedTransform=Z;this.bSearch=ka;this.mouseMove=function(a,b){setTimeout(function(){if(!N){var f= B.widgetCoordinates(y.canvas,b);if(ca(f,d.area))if(Y()&&Q){w=[f.x,f.y];Ca(ua)}}},0)};this.mouseDown=function(a,b){if(!N){a=B.widgetCoordinates(y.canvas,b);if(ca(a,d.area))F=a}};this.mouseUp=function(){N||(F=null)};this.mouseDrag=function(a,b){if(!N)if(F!==null){a=B.widgetCoordinates(y.canvas,b);if(ca(a,d.area)){B.buttons===1&&d.pan&&I({x:a.x-F.x,y:a.y-F.y});F=a}}};this.mouseWheel=function(a,b){a=d.wheelActions[(b.metaKey<<3)+(b.altKey<<2)+(b.ctrlKey<<1)+b.shiftKey];if(a!==undefined){var f=B.widgetCoordinates(y.canvas, b);if(ca(f,d.area)){var e=B.normalizeWheel(b);if((a===4||a===5||a===6)&&d.pan){f=j(h)[4];var g=j(i)[5];if(a===6)I({x:-e.pixelX,y:-e.pixelY});else if(a===5)I({x:0,y:-e.pixelX-e.pixelY});else a===4&&I({x:-e.pixelX-e.pixelY,y:0});if(f!==j(h)[4]||g!==j(i)[5])B.cancelEvent(b)}else if(d.zoom){B.cancelEvent(b);b=-e.spinY;if(b===0)b=-e.spinX;if(a===1)ja(f,0,b);else if(a===0)ja(f,b,0);else if(a===2)ja(f,b,b);else if(a===3)e.pixelX!==0?ja(f,b,0):ja(f,0,b)}}}};J.start=function(a,b){U=b.touches.length===1;V= b.touches.length===2;if(U){fa=false;a=B.widgetCoordinates(y.canvas,b.touches[0]);if(!ca(a,d.area))return;qa=Y()&&Ma(w,[a.x,a.y],30)?1:0;M=Date.now();F=a;B.capture(null);B.capture(y.canvas)}else if(V&&d.zoom){fa=false;A=[B.widgetCoordinates(y.canvas,b.touches[0]),B.widgetCoordinates(y.canvas,b.touches[1])].map(function(e){return[e.x,e.y]});if(!A.every(function(e){return ca(e,d.area)})){V=null;return}B.capture(null);B.capture(y.canvas);H=Math.atan2(A[1][1]-A[0][1],A[1][0]-A[0][0]);ya=[(A[0][0]+A[1][0])/ 2,(A[0][1]+A[1][1])/2];a=Math.abs(Math.sin(H));var f=Math.abs(Math.cos(H));H=a<Math.sin(0.125*Math.PI)?0:f<Math.cos(0.375*Math.PI)?Math.PI/2:Math.tan(H)>0?Math.PI/4:-Math.PI/4;za=La(H,ya)}else return;b.preventDefault&&b.preventDefault()};J.end=function(a,b){var f=Array.prototype.slice.call(b.touches),e=f.length===0;U=f.length===1;V=f.length===2;e||function(){var g;for(g=0;g<b.changedTouches.length;++g)(function(){for(var k=b.changedTouches[g].identifier,m=0;m<f.length;++m)if(f[m].identifier===k){f.splice(m, 1);return}})()}();e=f.length===0;U=f.length===1;V=f.length===2;if(e){ra=null;if(qa===0&&(isFinite(l.x)||isFinite(l.y))&&d.rubberBand){M=Date.now();fa=true;ma(va)}else{T.mouseUp(null,null);f=[];za=ya=H=null;if(M!=null){Date.now();M=null}}qa=null}else if(U||V)J.start(a,b)};var ra=null,Aa=null,Ha=null;J.moved=function(a,b){if(U||V)if(!(U&&F==null)){b.preventDefault&&b.preventDefault();Aa=B.widgetCoordinates(y.canvas,b.touches[0]);if(b.touches.length>1)Ha=B.widgetCoordinates(y.canvas,b.touches[1]);ra|| (ra=setTimeout(function(){if(U){var f=Aa,e=Date.now(),g={x:f.x-F.x,y:f.y-F.y},k=e-M;M=e;if(qa===1){w[h]+=g.x;w[i]+=g.y;Y()&&Q&&ma(ua)}else if(d.pan){l.x=g.x/k;l.y=g.y/k;I(g,d.rubberBand?Ga:0)}F=f}else if(V&&d.zoom){e=P(w);var m=(A[0][0]+A[1][0])/2,n=(A[0][1]+A[1][1])/2;f=[Aa,Ha].map(function(u){return H===0?[u.x,n]:H===Math.PI/2?[m,u.y]:C(za,[u.x,u.y])});g=Math.abs(A[1][0]-A[0][0]);k=Math.abs(f[1][0]-f[0][0]);var o=g>0?k/g:1;if(k===g||H===Math.PI/2)o=1;var z=(f[0][0]+f[1][0])/2;g=Math.abs(A[1][1]- A[0][1]);k=Math.abs(f[1][1]-f[0][1]);var s=g?k/g:1;if(k===g||H===0)s=1;var x=(f[0][1]+f[1][1])/2;d.isHorizontal&&function(){var u=o;o=s;s=u;u=z;z=x;x=u;u=m;m=n;n=u}();if(j(h)[0]*o>d.maxZoom[h])o=d.maxZoom[h]/j(h)[0];if(j(i)[3]*s>d.maxZoom[i])s=d.maxZoom[i]/j(i)[3];if(o!==1&&(o<1||j(h)[0]!==d.maxZoom[h]))pa(j(h),C([o,0,0,1,-o*m+z,0],j(h)));if(s!==1&&(s<1||j(i)[3]!==d.maxZoom[i]))pa(j(i),C([1,0,0,s,0,-s*n+x],j(i)));K();e=W(e);w[h]=e[h];w[i]=e[i];A=f;na();ba();ha()}ra=null},1))}};this.setXRange=function(a, b,f){b=d.modelArea[0]+d.modelArea[2]*b;f=d.modelArea[0]+d.modelArea[2]*f;if(b<p(d.modelArea))b=p(d.modelArea);if(f>t(d.modelArea))f=t(d.modelArea);var e=d.series[a];if(e.length!==0){a=W([b,0],true);var g=W([f,0],true),k=d.isHorizontal?i:h,m=d.isHorizontal?h:i,n=Ba(k,e),o=ka(a[k],e,n);if(n)if(o<0)o=0;else{o++;if(e[o][2]===X)o+=2}else if(o>=e.length-1)o=e.length-2;var z=ka(g[k],e,n);if(!n&&z<0)z=0;var s,x,u=Infinity,O=-Infinity;for(s=Math.min(o,z);s<=Math.max(o,z)&&s<e.length;++s)if(e[s][2]!==X&&e[s][2]!== aa){if(e[s][m]<u)u=e[s][m];if(e[s][m]>O)O=e[s][m]}if(n&&o>0||!n&&o<e.length-1){if(n){x=o-1;if(e[x][2]===aa)x-=2}else{x=o+1;if(e[x][2]===X)x+=2}s=(a[k]-e[x][k])/(e[o][k]-e[x][k]);o=e[x][m]+s*(e[o][m]-e[x][m]);if(o<u)u=o;if(o>O)O=o}if(n&&z<e.length-1||!n&&z>0){if(n){n=z+1;if(e[n][2]===X)n+=2}else{n=z-1;if(e[n][2]===aa)n-=2}s=(g[k]-e[z][k])/(e[n][k]-e[z][k]);o=e[z][m]+s*(e[n][m]-e[z][m]);if(o<u)u=o;if(o>O)O=o}b=d.modelArea[2]/(f-b);e=d.isHorizontal?2:3;f=d.area[e]/(O-u);f=d.area[e]/(d.area[e]/f+20); if(f>d.maxZoom[m])f=d.maxZoom[m];a=d.isHorizontal?[a[i]-q(d.area),(u+O)/2-d.area[2]/f/2-p(d.area)]:[a[h]-p(d.area),-((u+O)/2+d.area[3]/f/2-r(d.area))];m=P(w);j(h)[0]=b;j(i)[3]=f;j(h)[4]=-a[h]*b;j(i)[5]=-a[i]*f;a=W(m);w[h]=a[h];w[i]=a[i];K();na();ba();ha()}};this.getSeries=function(a){return d.series[a]};this.rangeChangedCallbacks=[];this.updateConfig=function(a){for(var b in a)if(a.hasOwnProperty(b))d[b]=a[b];Ka();na();ba();ha()};this.updateConfig({});if(window.TouchEvent&&!window.MSPointerEvent&& !window.PointerEvent){T.touchStart=J.start;T.touchEnd=J.end;T.touchMoved=J.moved}else{E=function(){};T.touchStart=E;T.touchEnd=E;T.touchMoved=E}}");
	}

	private static final int TICK_LENGTH = 5;

	static int toZoomLevel(double zoomFactor) {
		return (int) Math.floor(Math.log(zoomFactor) / Math.log(2.0) + 0.5) + 1;
	}
}
