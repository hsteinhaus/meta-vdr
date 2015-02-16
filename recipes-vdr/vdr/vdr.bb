DESCRIPTION = "Video Disk Recorder (VDR) is a digital sat-receiver program using Linux and DVB technologies. It allows to record broadcasts, as well as output the stream to TV."
SUMMARY = "Video Disk Recorder"
HOMEPAGE = "http://www.tvdr.de"
SECTION = "console/multimedia"
LICENSE = "GPLv2"
AUTHOR = "Klaus Schmidinger"

# the current version
PV = "2.1.10"

SRC_URI = "ftp://ftp.tvdr.de/vdr/Developer/${P}.tar.bz2"

SRC_URI[md5sum] = "889f053463e2720f1ad6517cc19e82f6"
SRC_URI[sha256sum] = "3a2f8f96586566dc40b7e11c252643957d1e2456914deddea1080240f58d5ad8"

LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

SRC_URI_append = " \
	file://remotetimers.patch \
	file://vdr-1.7.21-pluginmissing.patch \
	file://vdr-1.7.29-menuselection.patch \
	file://MainMenuHooks-v1_0_3.patch \
"

DEPENDS = " \
	fontconfig \
	freetype \
	ttf-bitstream-vera \
	gettext \
	jpeg \
	libcap \
	virtual/libintl \
	ncurses \
"

PLUGINDIR = "${libdir}/vdr"

CFLAGS += "-Wl,--hash-style=gnu -fPIC"

do_configure_append() {
    cat > Make.config <<-EOF
	## The C compiler options:
	CFLAGS   = ${CFLAGS} -Wall
	CXXFLAGS = ${CFLAGS} -Wall
	### The directory environment:
	PREFIX   = ${prefix}
	BINDIR   = ${bindir}
	INCDIR   = ${includedir}
	LIBDIR   = ${libdir}/vdr
	LOCDIR   = ${datadir}/locale
	MANDIR   = ${mandir}
	PCDIR    = ${libdir}/pkgconfig
	RESDIR   = ${datadir}/vdr

	VIDEODIR = /srv/vdr/video
	CONFDIR  = ${sysconfdir}/vdr
	ARGSDIR  = ${sysconfdir}/vdr/conf.d
	CACHEDIR = /var/cache/vdr
	EOF
}

# override oe_runmake: the -e in the original ignores Make.config...
oe_runmake () {
	bbnote make ${PARALLEL_MAKE} MAKEFLAGS= INCLUDES=-I${STAGING_INCDIR}/freetype2 vdr "$@"
	make ${PARALLEL_MAKE} MAKEFLAGS= INCLUDES=-I${STAGING_INCDIR}/freetype2 vdr "$@" || die "oe_runmake failed"
}

do_install () {
	oe_runmake 'DESTDIR=${D}' install-bin install-i18n install-includes install-pc
	cp Make.config ${D}${includedir}/vdr
}

FILES_${PN} = "${bindir}/* ${localstatedir}/cache/vdr ${datadir}/vdr "

FILES_${PN}-dbg += "${PLUGINDIR}/.debug/*"

FILES_${PN}-locale = "${datadir}/locale"
