package crtm

import crtm.abono.IVentaPrepagoTitulo
import crtm.abono.VentaPrepagoTitulo

val abonoClient: IVentaPrepagoTitulo by lazy { VentaPrepagoTitulo().basicHttpBindingIVentaPrepagoTitulo }